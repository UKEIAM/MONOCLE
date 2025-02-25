package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.EcogStatusDto;
import de.uke.iam.mtb.api.model.FamilyMemberDiagnosisDto;
import de.uke.iam.mtb.control.models.coredata.EcogStatus;
import de.uke.iam.mtb.control.models.coredata.FamilyMemberDiagnosis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EcogStatusMapper {

  @Mapping(target = "episodeId", source = "episode.id")
  EcogStatusDto toDto(EcogStatus ecogStatus);

  @Mapping(target = "episode.id", source = "episodeId")
  EcogStatus toEntity(EcogStatusDto ecogStatusDto);
}
