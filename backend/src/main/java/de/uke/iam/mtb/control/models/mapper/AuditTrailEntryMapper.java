package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.AuditTrailEntryDto;
import de.uke.iam.mtb.control.models.AuditTrailEntry;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditTrailEntryMapper {

  AuditTrailEntryDto toDto(AuditTrailEntry auditTrailEntry);

  AuditTrailEntry toEntity(AuditTrailEntryDto auditTrailEntryDto);
}
