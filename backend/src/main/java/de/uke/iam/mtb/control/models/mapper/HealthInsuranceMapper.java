package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.HealthInsuranceDto;
import de.uke.iam.mtb.control.models.HealthInsurance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HealthInsuranceMapper {

  @Mapping(target = "namenszeile1", source = "namenszeile_1")
  @Mapping(target = "namenszeile2", source = "namenszeile_2")
  @Mapping(target = "namenszeile3", source = "namenszeile_3")
  @Mapping(target = "namenszeile4", source = "namenszeile_4")
  @Mapping(target = "hStrasse", source = "h_Strasse")
  @Mapping(target = "HLKZ", source = "h_LKZ")
  @Mapping(target = "HPLZ", source = "h_PLZ")
  @Mapping(target = "hOrt", source = "h_Ort")
  @Mapping(target = "PLKZ", source = "h_LKZ")
  @Mapping(target = "PPLZ", source = "h_PLZ")
  @Mapping(target = "pOrt", source = "h_Ort")
  HealthInsuranceDto toDto(HealthInsurance healthInsurance);

  @Mapping(target = "namenszeile_1", source = "namenszeile1")
  @Mapping(target = "namenszeile_2", source = "namenszeile2")
  @Mapping(target = "namenszeile_3", source = "namenszeile3")
  @Mapping(target = "namenszeile_4", source = "namenszeile4")
  @Mapping(target = "h_Strasse", source = "hStrasse")
  @Mapping(target = "h_LKZ", source = "HLKZ")
  @Mapping(target = "h_PLZ", source = "HPLZ")
  @Mapping(target = "h_Ort", source = "hOrt")
  @Mapping(target = "p_LKZ", source = "PLKZ")
  @Mapping(target = "p_PLZ", source = "PPLZ")
  @Mapping(target = "p_Ort", source = "pOrt")
  HealthInsurance toEntity(HealthInsuranceDto healthInsuranceDto);
}
