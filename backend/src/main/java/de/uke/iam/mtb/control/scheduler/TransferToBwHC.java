package de.uke.iam.mtb.control.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uke.iam.mtb.api.model.AddPatientMlDto;
import de.uke.iam.mtb.api.model.DataOfAddPatientTokenMlDto;
import de.uke.iam.mtb.api.model.PatientCRUDEnumsDto;
import de.uke.iam.mtb.api.model.PatientIdMlDto;
import de.uke.iam.mtb.api.model.PatientTokenAckMlDto;
import de.uke.iam.mtb.api.model.PatientTokenMlDto;
import de.uke.iam.mtb.control.client.BwHealthCloud;
import de.uke.iam.mtb.control.client.Pseudonymization;
import de.uke.iam.mtb.control.exception.BwhcException;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.exception.PseudonymizationException;
import de.uke.iam.mtb.control.models.TransferJob;
import de.uke.iam.mtb.control.models.enums.JobStatus;
import de.uke.iam.mtb.control.repository.BwhcTransferRepository;
import de.uke.iam.mtb.control.service.BwhcTransferService;
import de.uke.iam.mtb.control.service.CoreDataService;
import de.uke.iam.mtb.control.service.IssueService;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@EnableAsync
@Transactional
public class TransferToBwHC {
    Logger logger = LoggerFactory.getLogger(TransferToBwHC.class);
    private final CoreDataService coreDataService;
    private final BwhcTransferService bwhcTransferService;
    private final IssueService issueService;
    private final BwHealthCloud bwHealthCloud;
    public final BwhcTransferRepository bwhcTransferRepository;
    Pseudonymization pseudonymization;
    private final String ML_GENRATED_ID;
    private final String MTB_UNIQUE_ID;


    public TransferToBwHC(CoreDataService coreDataService, BwhcTransferService bwhcTransferService, IssueService issueService, BwHealthCloud bwHealthCloud,
        BwhcTransferRepository bwhcTransferRepository, Pseudonymization pseudonymization, @Value("${ML_GENRATED_ID}") String mlGeneratedId,
        @Value("${MTB_UNIQUE_ID}") String mtbUniqueId) {
        this.coreDataService = coreDataService;
        this.bwhcTransferService = bwhcTransferService;
        this.issueService = issueService;
        this.bwHealthCloud = bwHealthCloud;
        this.bwhcTransferRepository = bwhcTransferRepository;
        this.pseudonymization = pseudonymization;
        this.ML_GENRATED_ID = mlGeneratedId;
        this.MTB_UNIQUE_ID = mtbUniqueId;
    }

    @Scheduled(fixedRateString = "${TRANS_BWHC_SCHEDULER_RATE_IN_SECONDS}", timeUnit = TimeUnit.SECONDS)
    public void startTransferProcess() {
        List<TransferJob> openJobs = bwhcTransferService.getAllOpenJobs();
        openJobs.forEach(job -> {
            logger.debug("startTransferProcess Job Status " + job.getStatus() + " for id " + job.getId());
            try {
                bwhcTransferService.updateStatus(job, JobStatus.INPROGRESS);
                // get core data from DB
                String coreData = coreDataService.getCoreData(job.getPatientId(), job.getEpisodeId());

                // Pseudoanonymize core data
                String pseudonym = getPseudonym(job.getEpisodeId().toString());
                JsonNode pseudoanonymizeCoreData = pseudoanonymizeCoreData(job.getPatientId().toString(), pseudonym, coreData);

                // send to bwhc
                String transferResponse = tranferToBwhc(pseudoanonymizeCoreData);

                if (transferResponse != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    // We get issues from bwHC
                    transferResponse = objectMapper.readTree(transferResponse).toPrettyString();

                    if (issueService.existsByEpisodeId(job.getEpisodeId())) {
                        issueService.updateIssue(job.getEpisodeId(), transferResponse);
                    } else {
                        issueService.addIssue(job.getEpisodeId(), transferResponse);
                    }
                } else {
                    if (issueService.existsByEpisodeId(job.getEpisodeId())) {
                        issueService.deleteIssueByEpisodeId(job.getEpisodeId());
                    }
                    logger.info("The dataset with patient pseudonym {} has no errors and is exported to bwHC.", pseudoanonymizeCoreData.get("patient").get("id") );
                }

                bwhcTransferService.updateStatus(job, JobStatus.DONE);

            } catch (ForeignKeyException | JsonProcessingException e) {
                logger.error("ForeignKeyException | JsonProcessingException");
                logger.error(e.getMessage());
                bwhcTransferService.updateStatus(job, JobStatus.ERROR);
            } catch (BwhcException e) {
                logger.error("BwhcException");
                logger.error(e.getStatusCode().toString(), e.getMessage());
                if (e.getStatusCode().is5xxServerError()) {
                    // bwhc is not available
                    bwhcTransferService.updateStatus(job, JobStatus.OPEN);
                } else if (e.getStatusCode().is4xxClientError()){
                    bwhcTransferService.updateStatus(job, JobStatus.ERROR);
                    ObjectMapper objectMapper = new ObjectMapper();
                    if (issueService.existsByEpisodeId(job.getEpisodeId())) {
                        issueService.updateIssue(job.getEpisodeId(), objectMapper.valueToTree(e.getResponseBody()).toString());
                    } else {
                        issueService.addIssue(job.getEpisodeId(), objectMapper.valueToTree(e.getResponseBody()).toString());
                    }
                } else {
                    logger.error(e.getStatusCode().toString());
                }
            } catch (PseudonymizationException e) {
                logger.error("PseudonymizationException");
                logger.error(e.getStatusCode().toString(), e.getMessage());
                if (e.getStatusCode().is5xxServerError()) {
                    // ML not available
                    bwhcTransferService.updateStatus(job, JobStatus.OPEN);
                } else if (e.getStatusCode().is4xxClientError()) {
                    bwhcTransferService.updateStatus(job, JobStatus.ERROR);
                } else {
                    logger.error(e.getStatusCode().toString());
                }
            }

        });
    }

    @Scheduled(fixedRateString = "${TRANS_BWHC_SCHEDULER_RATE_IN_SECONDS}", timeUnit = TimeUnit.SECONDS)
    public void deleteAllStatusDone() {
        bwhcTransferService.deleteAllByStatusDone();
    }

    public String tranferToBwhc(JsonNode coreData) throws BwhcException, JsonProcessingException {
        logger.debug(coreData.toPrettyString());
        return bwHealthCloud.sendJsonData(coreData);
    }

    // Get the token from the Mainzelliste to be used in the next requests
    public UUID getMlToken() throws PseudonymizationException, JsonProcessingException {
        // Create a new session to get a new token
        UUID sessionId = pseudonymization.createSession().getSessionId();
        // Create the body of the request to get a new Token
        DataOfAddPatientTokenMlDto data = new DataOfAddPatientTokenMlDto();
        data.setIdTypes(List.of(ML_GENRATED_ID));
        PatientTokenMlDto patientToken = new PatientTokenMlDto(PatientCRUDEnumsDto.ADDPATIENT, data);
        patientToken.setAllowedUses("1");
        // Send the request to get a new Token
        PatientTokenAckMlDto addPatientTokenResponse = pseudonymization.getPatientTokenResponse(
            sessionId, patientToken);
        return addPatientTokenResponse.getId();
    }

    // get the pseudonymized ID from the Mainzelliste
    public String getPseudonym(String patientId) throws PseudonymizationException, JsonProcessingException {
        String pseudonymizedId = null;
        // Get a new Token
        UUID addPatientToken = getMlToken();

        // Create the body of the request to get a new Pseudonym
        AddPatientMlDto newPatient = new AddPatientMlDto();
        List<String> idTypes = List.of(ML_GENRATED_ID);
        newPatient.setIdtypes(idTypes);
        Map<String, String> ids = new HashMap<>();
        ids.put(MTB_UNIQUE_ID, patientId);
        newPatient.setIds(ids);
        logger.debug("get Pseudonym");
        List<PatientIdMlDto> patientIds = pseudonymization.addPatient(addPatientToken, newPatient);

        // Filter the list to get only one id (ML_GENRATED_ID) to be sent to DNPM
        Optional<PatientIdMlDto> patientIdOptional = patientIds.stream().filter(
            patient -> patient.getIdType().equals(ML_GENRATED_ID)).findFirst();
        // TODO: The Mainzelliste gives an 201 status code with an empty list in body response even if the patient is not pseundonymized. HOW
        //  TO HANDLE THIS CASE?
        if (patientIdOptional.isPresent()) {
            pseudonymizedId = patientIdOptional.get().getIdString();
        }
        return pseudonymizedId;
    }

    public JsonNode pseudoanonymizeCoreData(String patientId, String pseudoanonymizedId, String coredata)
        throws JsonProcessingException {
        // Serialize core data to a JSON string using Jackson ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        String res = coredata.replace(patientId, pseudoanonymizedId);
        // This conversion is needed otherwise the JsonNode conversion does not work
        Map<String, Object> mapJson = objectMapper.readValue(res, new TypeReference<>() {});

        return objectMapper.valueToTree(mapJson);
    }
}