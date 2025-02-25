package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.AddressbookEntryDto;
import de.uke.iam.mtb.control.models.AddressbookEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressbookEntryMapper {

  @Mapping(target = "ukeinternal", source = "UKEinternal")
  AddressbookEntryDto toDto(AddressbookEntry addressbookEntry);

  @Mapping(target = "UKEinternal", source = "ukeinternal")
  AddressbookEntry toEntity(AddressbookEntryDto addressbookEntryDto);
}
