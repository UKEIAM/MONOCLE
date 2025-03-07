package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.RequirementDto;
import de.uke.iam.mtb.control.models.Requirement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressbookEntryMapper.class})
public interface RequirementMapper {

  @Mapping(target = "episodeId", source = "episode.id")
  RequirementDto toDto(Requirement requirement);

  @Mapping(target = "episode.id", source = "episodeId")
  Requirement toEntity(RequirementDto requirementDto);
}
