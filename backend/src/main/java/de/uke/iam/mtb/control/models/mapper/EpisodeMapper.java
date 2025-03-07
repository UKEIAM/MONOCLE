package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.EpisodeDto;
import de.uke.iam.mtb.control.models.Episode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = StepInfoMapper.class)
public interface EpisodeMapper {

  @Mappings({
      @Mapping(target = "workflowId", source = "workflow.id"),
      @Mapping(target = "patientId", source = "patient.id"),
      @Mapping(target = "stepInfo", source = "stepInfo"),
      @Mapping(target = "requirementId", source = "requirement.id")
  })
  EpisodeDto toDto(Episode comment);

  @Mappings({
      @Mapping(target = "workflow.id", source = "workflowId"),
      @Mapping(target = "patient.id", source = "patientId"),
      @Mapping(target = "stepInfo", source = "stepInfo"),
      @Mapping(target = "requirement.id", source = "requirementId")
  })
  Episode toEntity(EpisodeDto episodeDto);

}
