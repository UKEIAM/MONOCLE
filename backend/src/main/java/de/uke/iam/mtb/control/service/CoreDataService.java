package de.uke.iam.mtb.control.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.uke.iam.mtb.api.model.EpisodeDto;
import de.uke.iam.mtb.api.model.FamilyMemberDiagnosisDto;
import de.uke.iam.mtb.api.model.GuidelineTherapyDto;
import de.uke.iam.mtb.api.model.PatientDto;
import de.uke.iam.mtb.api.model.PeriodDto;
import de.uke.iam.mtb.api.model.SpecimenCollectionDto;
import de.uke.iam.mtb.api.model.SpecimenDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.service.coredata.CarePlanService;
import de.uke.iam.mtb.control.service.coredata.ClaimResponseService;
import de.uke.iam.mtb.control.service.coredata.ClaimService;
import de.uke.iam.mtb.control.service.coredata.DiagnoseService;
import de.uke.iam.mtb.control.service.coredata.EcogStatusService;
import de.uke.iam.mtb.control.service.coredata.FamilyMemberDiagnosisService;
import de.uke.iam.mtb.control.service.coredata.GeneticCounsellingRequestService;
import de.uke.iam.mtb.control.service.coredata.GuidelineTherapyService;
import de.uke.iam.mtb.control.service.coredata.HistologyReevaluationRequestService;
import de.uke.iam.mtb.control.service.coredata.HistologyReportService;
import de.uke.iam.mtb.control.service.coredata.MolecularPathologyFindingService;
import de.uke.iam.mtb.control.service.coredata.MolecularTherapyResponseService;
import de.uke.iam.mtb.control.service.coredata.MolecularTherapyService;
import de.uke.iam.mtb.control.service.coredata.NgsReportService;
import de.uke.iam.mtb.control.service.coredata.RebiopsyRequestService;
import de.uke.iam.mtb.control.service.coredata.SpecimenService;
import de.uke.iam.mtb.control.service.coredata.StudyInclusionRequestService;
import de.uke.iam.mtb.control.service.coredata.TherapyRecommendationService;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CoreDataService {
    private PatientService patientService;
    private EpisodeService episodeService;
    private DiagnoseService diagnoseService;
    private FamilyMemberDiagnosisService familyMemberDiagnosisService;
    private GuidelineTherapyService guidelineTherapyService;
    private EcogStatusService ecogStatusService;
    private SpecimenService specimenService;
    private MolecularPathologyFindingService molecularPathologyFindingService;
    private HistologyReportService histologyReportService;
    private NgsReportService ngsReportService;
    private CarePlanService carePlanService;
    private TherapyRecommendationService therapyRecommendationService;
    private GeneticCounsellingRequestService geneticCounsellingRequestService;
    private RebiopsyRequestService rebiopsyRequestService;
    private HistologyReevaluationRequestService histologyReevaluationRequestService;
    private StudyInclusionRequestService studyInclusionRequestService;
    private ClaimService claimService;
    private ClaimResponseService claimResponseService;
    private MolecularTherapyService molecularTherapyService;
    private MolecularTherapyResponseService molecularTherapyResponseService;

    public CoreDataService(PatientService patientService, EpisodeService episodeService, DiagnoseService diagnoseService,
        FamilyMemberDiagnosisService familyMemberDiagnosisService, GuidelineTherapyService guidelineTherapyService,
        EcogStatusService ecogStatusService, SpecimenService specimenService,
        MolecularPathologyFindingService molecularPathologyFindingService,
        HistologyReportService histologyReportService, NgsReportService ngsReportService, CarePlanService carePlanService,
        TherapyRecommendationService therapyRecommendationService, GeneticCounsellingRequestService geneticCounsellingRequestService,
        RebiopsyRequestService rebiopsyRequestService, HistologyReevaluationRequestService histologyReevaluationRequestService,
        StudyInclusionRequestService studyInclusionRequestService, ClaimService claimService,
        ClaimResponseService claimResponseService, MolecularTherapyService molecularTherapyService,
        MolecularTherapyResponseService molecularTherapyResponseService) {
        this.patientService = patientService;
        this.episodeService = episodeService;
        this.diagnoseService = diagnoseService;
        this.familyMemberDiagnosisService = familyMemberDiagnosisService;
        this.guidelineTherapyService = guidelineTherapyService;
        this.ecogStatusService = ecogStatusService;
        this.specimenService = specimenService;
        this.molecularPathologyFindingService = molecularPathologyFindingService;
        this.histologyReportService = histologyReportService;
        this.ngsReportService = ngsReportService;
        this.carePlanService = carePlanService;
        this.therapyRecommendationService = therapyRecommendationService;
        this.geneticCounsellingRequestService = geneticCounsellingRequestService;
        this.rebiopsyRequestService = rebiopsyRequestService;
        this.histologyReevaluationRequestService = histologyReevaluationRequestService;
        this.studyInclusionRequestService = studyInclusionRequestService;
        this.claimService = claimService;
        this.claimResponseService = claimResponseService;
        this.molecularTherapyService = molecularTherapyService;
        this.molecularTherapyResponseService = molecularTherapyResponseService;
    }

    public String getCoreData(UUID patientId, UUID episodeId) throws ForeignKeyException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Format date string
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();

        // Some Nodes must be modified so then we implement a function. Others can be converted to a ObjectNode directly form DB.
        objectNode.set("patient", getPatientObject(patientId, episodeId));
        objectNode.set("consent", getConsentObject(patientId, episodeId));
        objectNode.set("episode", getEpisodeObject(episodeId));
        objectNode.set("diagnoses", objectMapper.valueToTree(diagnoseService.getAllDiagnoses(episodeId)));
        objectNode.set("familyMemberDiagnoses", getFamilyMemberDiagnosesArrayNode(episodeId));
        // TODO probably will be guidelineTherapies and guidelineProcedures
//        objectNode.set("previousGuidelineTherapies", getPreviousGuidelineTherapiesArrayNode(episodeId));
//        objectNode.set("lastGuidelineTherapies", getLastGuidelineTherapiesArrayNode(episodeId));
        objectNode.set("ecogStatus", objectMapper.valueToTree(ecogStatusService.getAllEcogStatuss(episodeId)));
        objectNode.set("specimens", getSpecimenArrayNode(episodeId));
        objectNode.set("molecularPathologyFindings", objectMapper.valueToTree(molecularPathologyFindingService.getAllMolecularPathologyFindings(episodeId)));
        objectNode.set("histologyReports", objectMapper.valueToTree(histologyReportService.getAllHistologyReports(episodeId)));
        objectNode.set("ngsReports", objectMapper.valueToTree(ngsReportService.getAllNgsReports(episodeId)));
        objectNode.set("carePlans", objectMapper.valueToTree(carePlanService.getAllCarePlans(episodeId)));
        objectNode.set("recommendations", objectMapper.valueToTree(therapyRecommendationService.getAllTherapyRecommendations(episodeId)));
        objectNode.set("geneticCounsellingRequests", objectMapper.valueToTree(geneticCounsellingRequestService.getAllGeneticCounsellingRequests(episodeId)));
        objectNode.set("rebiopsyRequests", objectMapper.valueToTree(rebiopsyRequestService.getAllRebiopsyRequests(episodeId)));
        objectNode.set("histologyReevaluationRequests", objectMapper.valueToTree(histologyReevaluationRequestService.getAllHistologyReevaluationRequests(episodeId)));
        objectNode.set("studyInclusionRequests", objectMapper.valueToTree(studyInclusionRequestService.getAllStudyInclusionRequests(episodeId)));
        objectNode.set("claims", objectMapper.valueToTree(claimService.getAllClaims(episodeId)));
        objectNode.set("claimResponses", objectMapper.valueToTree(claimResponseService.getAllClaimResponses(episodeId)));
        objectNode.set("molecularTherapies", getMolecularTherapiesArrayNode(episodeId));
        objectNode.set("responses", objectMapper.valueToTree(molecularTherapyResponseService.getAllMolecularTherapyResponses(episodeId)));

        // replace the patient.id (currently episodeId) with the patientId
        String patientCoreData =
            objectNode.toString().replace(String.format("\"id\":\"%s\"", episodeId), String.format("\"id\":\"%s\"", patientId));
        // replace all episodeId: episodeid with patient: patinetId
        patientCoreData =
            patientCoreData.replace(String.format("\"episodeId\":\"%s\"", episodeId), String.format("\"patient\":\"%s\"", patientId));

        return patientCoreData;
    }

    private ObjectNode getPatientObject(UUID patientId, UUID episodeId) {
        ObjectMapper objectMapper = new ObjectMapper();
        // Create an ObjectNode representing a JSON object
        ObjectNode objectNode = objectMapper.createObjectNode();
        PatientDto patientDto = patientService.getPatient(patientId);
        // It Should be th episode id to be replaced with the pseudonymized patientId
        objectNode.put("id", episodeId.toString());
        objectNode.put("gender", patientDto.getGender().toString());
        objectNode.put("birthDate", patientDto.getDateOfBirth().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        objectNode.put("insurance", patientDto.getHealthInsurance().toString());
        return objectNode;
    }

    private ObjectNode getConsentObject(UUID patientId, UUID episodeId) {
        ObjectMapper objectMapper = new ObjectMapper();
        // Create an ObjectNode representing a JSON object
        ObjectNode objectNode = objectMapper.createObjectNode();
        PatientDto patientDto = patientService.getPatient(patientId);
        objectNode.put("id", UUID.randomUUID().toString());
        objectNode.put("episodeId", episodeId.toString());
        objectNode.put("status", getConsentStatus(patientDto.getConsent()));
        return objectNode;
    }

    private String getConsentStatus(boolean consent) {
        if (consent == true) {
            return "active";
        } else {
            return "rejected";
        }
    }

    private ObjectNode getEpisodeObject(UUID episodeId) {
        ObjectMapper objectMapper = new ObjectMapper();
        // Create an ObjectNode representing a JSON object
        ObjectNode episodeNode = objectMapper.createObjectNode();
        EpisodeDto episodeDto = episodeService.getEpisode(episodeId);
        episodeNode.put("id", UUID.randomUUID().toString());
        episodeNode.put("episodeId", episodeId.toString());
        ObjectNode periodNode = objectMapper.createObjectNode();
        periodNode.put("start", LocalDate.ofInstant(episodeDto.getCreatedAt(),ZoneId.of("Europe/Berlin")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        episodeNode.set("period", periodNode);
        return episodeNode;
    }

    private ArrayNode getFamilyMemberDiagnosesArrayNode(UUID episodeId) throws ForeignKeyException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // Create an ArrayNode for family member diagnoses
        ArrayNode familyMemberDiagnosesArrayNode = objectMapper.createArrayNode();
        // Fetch the list of FamilyMemberDiagnosisDto from the service
        List<FamilyMemberDiagnosisDto> familyMemberDiagnosesList = familyMemberDiagnosisService.getAllFamilyMemberDiagnosis(episodeId);

        // Iterate over the list and add each FamilyMemberDiagnosisDto as a separate element in the JSON array
        for (FamilyMemberDiagnosisDto familyMemberDiagnosisDto : familyMemberDiagnosesList) {
            ObjectNode object = objectMapper.valueToTree(familyMemberDiagnosisDto);
            object.put("episodeId", episodeId.toString());
            object.remove("details");
            // Add the JSON node to the ArrayNode
            familyMemberDiagnosesArrayNode.add(object);
        }
        return familyMemberDiagnosesArrayNode;
    }

    //TODO probably will be guidelineTherapies and guidelineProcedures
//    private ArrayNode getPreviousGuidelineTherapiesArrayNode(UUID episodeId) throws ForeignKeyException {
//        List<GuidelineTherapyDto> previousGuidelineTherapies = guidelineTherapyService.getAllPreviousGuidelineTherapies(episodeId);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//
//        // Create an ArrayNode to hold the converted objects
//        ArrayNode previousGuidelineTherapiesArrayNode = objectMapper.createArrayNode();
//        objectMapper.registerModule(new JavaTimeModule());
//        // Convert each GuidelineTherapy in the list to JsonNode with specific fields and add to the ArrayNode
//        previousGuidelineTherapies.forEach(previousGuidelineTherapy -> {
//            ObjectNode previousGuidelineTherapyNode = objectMapper.createObjectNode();
//            previousGuidelineTherapyNode.put("id", previousGuidelineTherapy.getId().toString());
//            previousGuidelineTherapyNode.put("episodeId", episodeId.toString());
//            previousGuidelineTherapyNode.put("diagnosis", previousGuidelineTherapy.getDiagnosis().toString());
//            previousGuidelineTherapyNode.put("therapyLine",
//                previousGuidelineTherapy.getTherapyLine() != null ? previousGuidelineTherapy.getTherapyLine().toBigInteger() : null);
//            previousGuidelineTherapyNode.set("medication", objectMapper.valueToTree(previousGuidelineTherapy.getMedication()));
//
//            previousGuidelineTherapiesArrayNode.add(previousGuidelineTherapyNode);
//        });
//
//        return previousGuidelineTherapiesArrayNode;
//    }
//
//    private ArrayNode getLastGuidelineTherapiesArrayNode(UUID episodeId) throws ForeignKeyException {
//        List<GuidelineTherapyDto> lastGuidelineTherapiesList = guidelineTherapyService.getAllLastGuidelineTherapies(episodeId);
//        // Create an ArrayNode for last guideline therapies
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//
//        // Create an ArrayNode to hold the converted objects
//        ArrayNode lastGuidelineTherapiesArrayNode = objectMapper.createArrayNode();
//
//        // Convert each GuidelineTherapy in the list to JsonNode with specific fields and add to the ArrayNode
//        lastGuidelineTherapiesList.forEach(lastGuidelineTherapy -> {
//            ObjectNode lastGuidelineTherapyNode = objectMapper.createObjectNode();
//            lastGuidelineTherapyNode.put("id", lastGuidelineTherapy.getId().toString());
//            lastGuidelineTherapyNode.put("episodeId", episodeId.toString());
//            lastGuidelineTherapyNode.put("diagnosis", lastGuidelineTherapy.getDiagnosis().toString());
//            lastGuidelineTherapyNode.put("therapyLine",
//                lastGuidelineTherapy.getTherapyLine() != null ? lastGuidelineTherapy.getTherapyLine().toBigInteger() : null);
//            lastGuidelineTherapyNode.set("medication", objectMapper.valueToTree(lastGuidelineTherapy.getMedication()));
//
//            ObjectNode periodNode = objectMapper.createObjectNode();
//            periodNode.put("start", lastGuidelineTherapy.getPeriod().getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//            if (lastGuidelineTherapy.getPeriod().getEnd() != null) {
//                periodNode.put("end", lastGuidelineTherapy.getPeriod().getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//            }
//            lastGuidelineTherapyNode.replace("period", periodNode);
//            if (lastGuidelineTherapy.getReasonStopped().getCode() != null) {
//                lastGuidelineTherapyNode.set("reasonStopped", objectMapper.valueToTree(lastGuidelineTherapy.getReasonStopped()));
//            }
//            lastGuidelineTherapiesArrayNode.add(lastGuidelineTherapyNode);
//        });
//
//        return lastGuidelineTherapiesArrayNode;
//    }

    private ArrayNode getSpecimenArrayNode(UUID episodeId) throws ForeignKeyException {
        List<SpecimenDto> specimenList = specimenService.getAllSpecimens(episodeId);
        // Create an ArrayNode for specimen
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Create an ArrayNode to hold the converted objects
        ArrayNode specimenArrayNode = objectMapper.createArrayNode();

        specimenList.forEach(specimen -> {
            ObjectNode object = objectMapper.valueToTree(specimen);
            object.put("episodeId", episodeId.toString());
            SpecimenCollectionDto collection = specimen.getCollection();
            if (collection.getDate() == null && collection.getLocalization() == null && collection.getMethod() == null) {
                object.remove("collection");
            } else {
                if (specimen.getCollection().getDate() != null) {
                    object.withObject("/collection")
                        .put("date", specimen.getCollection().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
            }
            specimenArrayNode.add(object);
        });

        return specimenArrayNode;
    }

    private ArrayNode getMolecularTherapiesArrayNode(UUID episodeId) throws ForeignKeyException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ArrayNode arrayNode = objectMapper.createArrayNode();
        molecularTherapyService.getAllMolecularTherapies(episodeId).forEach(dto -> {
            // Change format/structure of element to match bwhc requirements and add it to the array
            ObjectNode history0 = objectMapper.valueToTree(dto);
            history0.put("episodeId", episodeId.toString());
            history0.put("recordedOn", dto.getRecordedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            // period
            PeriodDto periodDto = dto.getPeriod();
            ObjectNode period = objectMapper.valueToTree(periodDto);
            if (periodDto.getStart() != null) {
                period.put("start", periodDto.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (periodDto.getEnd() != null) {
                period.put("end", periodDto.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            // TODO bwhc likely requires the removal of nested objects/fields that are null elsewhere
            if (periodDto.getStart() != null || periodDto.getEnd() != null) {
                history0.set("period", period);
            } else {
                history0.remove("period");
            }
            if (dto.getReasonStopped() == null || dto.getReasonStopped().getCode() == null) {
                history0.remove("reasonStopped");
            }
            if (dto.getNotDoneReason() == null || dto.getNotDoneReason().getCode() == null) {
                history0.remove("notDoneReason");
            }

            /* wrap history0 so that the final result is in the format of
              [{history: [history0]}, {history: [history0]}, ...]
              according to https://uke-iam.atlassian.net/wiki/spaces/MD/pages/226885669/Beispieldatensatz
             */
            ArrayNode history = objectMapper.createArrayNode();
            history.add(history0);
            ObjectNode molecularTherapy = objectMapper.createObjectNode();
            molecularTherapy.set("history", history);

            arrayNode.add(molecularTherapy);
        });

        return arrayNode;
    }
}
