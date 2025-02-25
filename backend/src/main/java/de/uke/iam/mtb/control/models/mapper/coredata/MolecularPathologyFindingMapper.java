package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.MolecularPathologyFindingDto;
import de.uke.iam.mtb.control.models.coredata.MolecularPathologyFinding;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MolecularPathologyFindingMapper {

  @Mapping(target = "episodeId", source = "episode.id")
  @Mapping(target = "specimen", source = "specimen.id")
  MolecularPathologyFindingDto toDto(MolecularPathologyFinding molecularPathologyFinding);

  @Mapping(target = "episode.id", source = "episodeId")
  @Mapping(target = "specimen.id", source = "specimen")
  MolecularPathologyFinding toEntity(MolecularPathologyFindingDto molecularPathologyFindingDto);

}
