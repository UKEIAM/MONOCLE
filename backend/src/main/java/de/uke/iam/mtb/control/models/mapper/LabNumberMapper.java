package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.LabNumberDto;
import de.uke.iam.mtb.control.models.LabNumber;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LabNumberMapper {

  LabNumberDto toDto(LabNumber labNumber);

  LabNumber toEntity(LabNumberDto labNumberDto);
}
