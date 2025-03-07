package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.CarePlanDto;
import de.uke.iam.mtb.control.models.coredata.CarePlan;
import de.uke.iam.mtb.control.models.coredata.RebiopsyRequest;
import de.uke.iam.mtb.control.models.coredata.StudyInclusionRequest;
import de.uke.iam.mtb.control.models.coredata.TherapyRecommendation;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CarePlanMapper {

    @Named("TherapyRecommendationsToUUIDs")
    static List<UUID> TherapyRecommendationsToUUIDs(List<TherapyRecommendation> therapyRecommendations) {
        if (therapyRecommendations == null) {
            return Collections.emptyList();
        }
        return therapyRecommendations.stream()
            .map(TherapyRecommendation::getId)
            .collect(Collectors.toList());
    }

    @Named("UUIDsToTherapyRecommendations")
    static List<TherapyRecommendation> UUIDsToTherapyRecommendations(List<UUID> therapyRecommendations) {
        if (therapyRecommendations == null) {
            return Collections.emptyList();
        }
        return therapyRecommendations.stream()
            .map(id -> {
                TherapyRecommendation therapyRecommendation = new TherapyRecommendation();
                therapyRecommendation.setId(id);
                return therapyRecommendation;
            })
            .collect(Collectors.toList());
    }

    @Named("RebiopsyRequestsToUUIDs")
    static List<UUID> RebiopsyRequestsToUUIDs(List<RebiopsyRequest> rebiopsyRequests) {
        if (rebiopsyRequests == null) {
            return Collections.emptyList();
        }
        return rebiopsyRequests.stream()
            .map(RebiopsyRequest::getId)
            .collect(Collectors.toList());
    }

    @Named("UUIDsToRebiopsyRequests")
    static List<RebiopsyRequest> UUIDsToRebiopsyRequests(List<UUID> rebiopsyRequests) {
        if (rebiopsyRequests == null) {
            return Collections.emptyList();
        }
        return rebiopsyRequests.stream()
            .map(id -> {
                RebiopsyRequest rebiopsyRequest = new RebiopsyRequest();
                rebiopsyRequest.setId(id);
                return rebiopsyRequest;
            })
            .collect(Collectors.toList());
    }

    @Named("StudyInclusionRequestsToUUIDs")
    static List<UUID> StudyInclusionRequestsToUUIDs(List<StudyInclusionRequest> studyInclusionRequests) {
        if (studyInclusionRequests == null) {
            return Collections.emptyList();
        }
        return studyInclusionRequests.stream()
            .map(StudyInclusionRequest::getId)
            .collect(Collectors.toList());
    }

    @Named("UUIDsToStudyInclusionRequests")
    static List<StudyInclusionRequest> UUIDsToStudyInclusionRequests(List<UUID> studyInclusionRequests) {
        if (studyInclusionRequests == null) {
            return Collections.emptyList();
        }
        return studyInclusionRequests.stream()
            .map(id -> {
                StudyInclusionRequest studyInclusionRequest = new StudyInclusionRequest();
                studyInclusionRequest.setId(id);
                return studyInclusionRequest;
            })
            .collect(Collectors.toList());
    }

    @Mapping(target = "episodeId", source = "episode.id")
    @Mapping(target = "diagnosis", source = "diagnosis.id")
    @Mapping(target = "geneticCounsellingRequest.episodeId", source = "geneticCounsellingRequest.episode.id")
    @Mapping(target = "recommendations", source = "recommendations", qualifiedByName = "TherapyRecommendationsToUUIDs")
    @Mapping(target = "rebiopsyRequests", source = "rebiopsyRequests", qualifiedByName = "RebiopsyRequestsToUUIDs")
    @Mapping(target = "studyInclusionRequests", source = "studyInclusionRequests", qualifiedByName = "StudyInclusionRequestsToUUIDs")
    CarePlanDto toDto(CarePlan carePlan);

    @Mapping(target = "episode.id", source = "episodeId")
    @Mapping(target = "diagnosis.id", source = "diagnosis")
    @Mapping(target = "geneticCounsellingRequest.episode.id", source = "geneticCounsellingRequest.episodeId")
    @Mapping(target = "recommendations", source = "recommendations", qualifiedByName = "UUIDsToTherapyRecommendations")
    @Mapping(target = "rebiopsyRequests", source = "rebiopsyRequests", qualifiedByName = "UUIDsToRebiopsyRequests")
    @Mapping(target = "studyInclusionRequests", source = "studyInclusionRequests", qualifiedByName = "UUIDsToStudyInclusionRequests")
    CarePlan toEntity(CarePlanDto carePlanDto);
}
