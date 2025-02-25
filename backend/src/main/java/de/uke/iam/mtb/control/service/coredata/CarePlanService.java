package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.CarePlanDto;
import de.uke.iam.mtb.api.model.GeneticCounsellingRequestDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.coredata.CarePlan;
import de.uke.iam.mtb.control.models.coredata.GeneticCounsellingRequest;
import de.uke.iam.mtb.control.models.mapper.coredata.CarePlanMapper;
import de.uke.iam.mtb.control.models.mapper.coredata.GeneticCounsellingRequestMapper;
import de.uke.iam.mtb.control.repository.coredata.CarePlanRepository;
import de.uke.iam.mtb.control.repository.coredata.GeneticCounsellingRequestRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class CarePlanService {

    private final GeneticCounsellingRequestMapper geneticCounsellingRequestMapper;
    private final CarePlanRepository carePlanRepository;
    private final CarePlanMapper carePlanMapper;
    private final EpisodeService episodeService;
    private final DiagnoseService diagnoseService;
    private final TherapyRecommendationService therapyRecommendationService;
    private final GeneticCounsellingRequestService geneticCounsellingRequestService;
    private final RebiopsyRequestService rebiopsyRequestService;
    private final StudyInclusionRequestService studyInclusionRequestService;
    private final GeneticCounsellingRequestRepository geneticCounsellingRequestRepository;

    public boolean isTherapyRecommendationsExists(List<UUID> therapyRecommendationIds) {
        if (therapyRecommendationIds == null) {
            return true;
        }
        return therapyRecommendationIds.stream().allMatch(therapyRecommendationService::isTherapyRecommendationExist);
    }

    public boolean isRebiopsyRequestsExists(List<UUID> rebiopsyRequestIds) {
        if (rebiopsyRequestIds == null) {
            return true;
        }
        return rebiopsyRequestIds.stream().allMatch(rebiopsyRequestService::isRebiopsyRequestExist);
    }

    public boolean isStudyInclusionRequestsExists(List<UUID> studyInclusionRequestIds) {
        if (studyInclusionRequestIds == null) {
            return true;
        }
        return studyInclusionRequestIds.stream().allMatch(studyInclusionRequestService::isStudyInclusionRequestExist);
    }

    public CarePlanDto addCarePlan(CarePlanDto carePlanDto) throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(carePlanDto.getEpisodeId())) {
            throw new ForeignKeyException("Episode with ID " + carePlanDto.getEpisodeId() + " does not exist");
        }

        if (!diagnoseService.isDiagnoseExist(carePlanDto.getDiagnosis())) {
            throw new ForeignKeyException("Diagnose with ID " + carePlanDto.getDiagnosis() + " does not exist");
        }

        if (!isTherapyRecommendationsExists(carePlanDto.getRecommendations())) {
            throw new ForeignKeyException("TherapyRecommendation with ID " + carePlanDto.getRecommendations() + " does not exist");
        }

        if (carePlanDto.getGeneticCounsellingRequest() != null &&
            carePlanDto.getGeneticCounsellingRequest().getId() != null &&
            !geneticCounsellingRequestService.isGeneticCounsellingRequestExist(carePlanDto.getGeneticCounsellingRequest().getId())) {
            throw new ForeignKeyException(
                "GeneticCounsellingRequest with ID " + carePlanDto.getGeneticCounsellingRequest() + " does not exist");
        }

        if (!isRebiopsyRequestsExists(carePlanDto.getRebiopsyRequests())) {
            throw new ForeignKeyException("RebiopsyRequest with ID " + carePlanDto.getRebiopsyRequests() + " does not exist");
        }

        if (!isStudyInclusionRequestsExists(carePlanDto.getStudyInclusionRequests())) {
            throw new ForeignKeyException("StudyInclusionRequest with ID " + carePlanDto.getStudyInclusionRequests() + " does not exist");
        }

        CarePlan carePlan = carePlanMapper.toEntity(carePlanDto);

        if (carePlanDto.getGeneticCounsellingRequest() != null && carePlanDto.getGeneticCounsellingRequest().getId() == null) {
            // Add genetic counselling Request
            GeneticCounsellingRequestDto savedGeneticCounsellingRequestDto = geneticCounsellingRequestService.addGeneticCounsellingRequest(
                carePlanDto.getGeneticCounsellingRequest());
            // need to be set and saved otherwise error: object references an unsaved transient instance - save the transient instance before flushing
            carePlan.setGeneticCounsellingRequest(geneticCounsellingRequestMapper.toEntity(savedGeneticCounsellingRequestDto));
        } else {
            carePlan.setGeneticCounsellingRequest(null);
        }

        CarePlan savedCarePlan = carePlanRepository.save(carePlan);

        return carePlanMapper.toDto(savedCarePlan);
    }

    public boolean isCarePlanExist(UUID carePlanId) {
        return carePlanRepository.existsById(carePlanId);
    }

    public CarePlanDto getCarePlan(UUID carePlanId) {
        CarePlan savedCarePlan = carePlanRepository.getReferenceById(carePlanId);
        return carePlanMapper.toDto(savedCarePlan);
    }

    public List<CarePlanDto> getAllCarePlans(UUID episodeId) throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(episodeId)) {
            throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
        }

        List<CarePlan> SavedCarePlans = carePlanRepository.getAllByEpisodeId(episodeId);

        return SavedCarePlans.stream().map(carePlanMapper::toDto).toList();
    }

    public CarePlanDto updateCarePlan(UUID carePlanId, CarePlanDto carePlanDto) throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(carePlanDto.getEpisodeId())) {
            throw new ForeignKeyException("Episode with ID " + carePlanDto.getEpisodeId() + " does not exist");
        }

        if (!diagnoseService.isDiagnoseExist(carePlanDto.getDiagnosis())) {
            throw new ForeignKeyException("Diagnose with ID " + carePlanDto.getDiagnosis() + " does not exist");
        }

        if (!isTherapyRecommendationsExists(carePlanDto.getRecommendations())) {
            throw new ForeignKeyException("TherapyRecommendation with ID " + carePlanDto.getRecommendations() + " does not exist");
        }

        if (carePlanDto.getGeneticCounsellingRequest() != null && carePlanDto.getGeneticCounsellingRequest().getId() != null &&
            !geneticCounsellingRequestService.isGeneticCounsellingRequestExist(carePlanDto.getGeneticCounsellingRequest().getId())) {
            throw new ForeignKeyException(
                "GeneticCounsellingRequest with ID " + carePlanDto.getGeneticCounsellingRequest() + " does not exist");
        }

        if (!isRebiopsyRequestsExists(carePlanDto.getRebiopsyRequests())) {
            throw new ForeignKeyException("RebiopsyRequest with ID " + carePlanDto.getRebiopsyRequests() + " does not exist");
        }

        if (!isStudyInclusionRequestsExists(carePlanDto.getStudyInclusionRequests())) {
            throw new ForeignKeyException("StudyInclusionRequest with ID " + carePlanDto.getStudyInclusionRequests() + " does not exist");
        }

        CarePlan carePlan = carePlanMapper.toEntity(carePlanDto);
        Optional<CarePlan> previousCarePlan = carePlanRepository.findById(carePlanId);
        GeneticCounsellingRequest previousGeneticCounsellingRequest = previousCarePlan.map(CarePlan::getGeneticCounsellingRequest)
            .orElse(null);
        UUID geneticCounsellingRequestIdToBeDeleted = null;

        if (carePlanDto.getGeneticCounsellingRequest() != null) {
            GeneticCounsellingRequestDto savedGeneticCounsellingRequestDto;

            if (previousGeneticCounsellingRequest != null ) {
                // a check (|| carePlanDto.getGeneticCounsellingRequest().getId() != null) is not necessary, because in the lines above it will
                // be checked if the ID from the request already exists.
                // If the previous entry in the database has an ID for the genetic counselling request, use the existing ID from the database.
                carePlanDto.getGeneticCounsellingRequest().setId(previousGeneticCounsellingRequest.getId());
                // Update existing geneticCounsellingRequest
                savedGeneticCounsellingRequestDto = geneticCounsellingRequestService.updateGeneticCounsellingRequest(
                    carePlanDto.getGeneticCounsellingRequest().getId(), carePlanDto.getGeneticCounsellingRequest());
            } else {
                // Add new geneticCounsellingRequest
                savedGeneticCounsellingRequestDto = geneticCounsellingRequestService.addGeneticCounsellingRequest(
                    carePlanDto.getGeneticCounsellingRequest());
            }

            carePlan.setGeneticCounsellingRequest(geneticCounsellingRequestMapper.toEntity(savedGeneticCounsellingRequestDto));
        } else {
            // delete geneticCounsellingRequest if it was present
            if (previousGeneticCounsellingRequest != null && previousGeneticCounsellingRequest.getId() != null) {
                // genetic counselling request cannot be deleted as long as it is referenced in carePlan
                geneticCounsellingRequestIdToBeDeleted = previousGeneticCounsellingRequest.getId();
                carePlan.setGeneticCounsellingRequest(null);
            }
        }

        CarePlan updatedCarePlan = carePlanRepository.save(carePlan);

        if (geneticCounsellingRequestIdToBeDeleted != null) {
            geneticCounsellingRequestService.deleteGeneticCounsellingRequest(geneticCounsellingRequestIdToBeDeleted);
        }

        return carePlanMapper.toDto(updatedCarePlan);
    }

    public void deleteCarePlan(UUID carePlanId) {
        CarePlanDto carePlan = getCarePlan(carePlanId);
        if (carePlan.getGeneticCounsellingRequest() != null) {
            geneticCounsellingRequestRepository.deleteById(carePlan.getGeneticCounsellingRequest().getId());
        }
        carePlanRepository.deleteById(carePlanId);
    }
}
