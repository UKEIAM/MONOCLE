package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.FamilyMemberDiagnosisDto;
import de.uke.iam.mtb.api.model.SpecimenDto;
import de.uke.iam.mtb.control.models.coredata.FamilyMemberDiagnosis;
import de.uke.iam.mtb.control.models.coredata.Specimen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FamilyMemberDiagnosisMapper {

  @Mapping(target = "episodeId", source = "episode.id")
  FamilyMemberDiagnosisDto toDto(FamilyMemberDiagnosis familyMemberDiagnosis);

  @Mapping(target = "episode.id", source = "episodeId")
  FamilyMemberDiagnosis toEntity(FamilyMemberDiagnosisDto familyMemberDiagnosisDto);
}
