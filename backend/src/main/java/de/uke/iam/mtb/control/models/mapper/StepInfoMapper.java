package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.StepInfoDto;
import de.uke.iam.mtb.control.models.StepInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface StepInfoMapper {

  @Mappings({
      @Mapping(target = "stepId", source = "step.id"),
      @Mapping(target = "episodeId", source = "episode.id"),
  })
  StepInfoDto toDto(StepInfo stepInfo);

  @Mappings({
      @Mapping(target = "step.id", source = "stepId"),
      @Mapping(target = "episode.id", source = "episodeId"),
  })
  StepInfo toEntity(StepInfoDto stepInfoDto);

}
