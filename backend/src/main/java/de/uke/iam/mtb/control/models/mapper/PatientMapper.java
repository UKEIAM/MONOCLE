package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.PatientDto;
import de.uke.iam.mtb.control.models.HealthInsurance;
import de.uke.iam.mtb.control.models.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {HealthInsurance.class, EpisodeMapper.class})
public interface PatientMapper {

  @Mapping(target = "healthInsurance", source = "healthInsurance.IK")
  PatientDto toDto(Patient patient);

  @Mapping(target = "healthInsurance.IK", source = "healthInsurance")
  Patient toEntity(PatientDto patientDto);

}
