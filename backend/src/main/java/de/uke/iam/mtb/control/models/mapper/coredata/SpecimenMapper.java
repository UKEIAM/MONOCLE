package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.SpecimenDto;
import de.uke.iam.mtb.control.models.coredata.Specimen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpecimenMapper {

  @Mapping(target = "episodeId", source = "episode.id")
  SpecimenDto toDto(Specimen specimen);

  @Mapping(target = "episode.id", source = "episodeId")
  Specimen toEntity(SpecimenDto SpecimenDto);
}
