package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.GuidelineTherapyDto;
import de.uke.iam.mtb.control.models.coredata.GuidelineTherapy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GuidelineTherapyMapper {

  @Mapping(target = "episodeId", source = "episode.id")
  @Mapping(target = "diagnosis", source = "diagnosis.id")
  @Mapping(target = "molecularTherapyResponse", source = "molecularTherapyResponse.id")
  @Mapping(target = "period.start", source = "periodStart")
  @Mapping(target = "period.end", source = "periodEnd")
  GuidelineTherapyDto toDto(GuidelineTherapy guidelineTherapy);

  @Mapping(target = "episode.id", source = "episodeId")
  @Mapping(target = "diagnosis.id", source = "diagnosis")
  @Mapping(target = "molecularTherapyResponse.id", source = "molecularTherapyResponse")
  @Mapping(target = "periodStart", source = "period.start")
  @Mapping(target = "periodEnd", source = "period.end")
  GuidelineTherapy toEntity(GuidelineTherapyDto guidelineTherapyDto);
}
