package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.TherapyRecommendationDto;
import de.uke.iam.mtb.control.models.coredata.TherapyRecommendation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TherapyRecommendationMapper {

  @Mapping(target = "episodeId", source = "episode.id")
  @Mapping(target = "diagnosis", source = "diagnosis.id")
  @Mapping(target = "ngsReport", source = "ngsReport.id")
  TherapyRecommendationDto toDto(TherapyRecommendation therapyRecommendation);

  @Mapping(target = "episode.id", source = "episodeId")
  @Mapping(target = "diagnosis.id", source = "diagnosis")
  @Mapping(target = "ngsReport.id", source = "ngsReport")
  TherapyRecommendation toEntity(TherapyRecommendationDto therapyRecommendationDto);

}
