package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.api.model.AddressbookEntryDto;
import de.uke.iam.mtb.control.models.AddressbookEntry;
import de.uke.iam.mtb.control.models.mapper.AddressbookEntryMapper;
import de.uke.iam.mtb.control.repository.AddressbookRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AddressbookService {

  private final AddressbookRepository addressbookRepository;
  private final AddressbookEntryMapper addressbookEntryMapper;


  public AddressbookService(AddressbookRepository addressbookRepository, AddressbookEntryMapper addressbookEntryMapper) {
    this.addressbookRepository = addressbookRepository;
    this.addressbookEntryMapper = addressbookEntryMapper;
  }

  public AddressbookEntryDto addAddressbookEntry(AddressbookEntryDto addressbookEntryDto) {
    if (addressbookEntryDto.getId() == null) {
      addressbookEntryDto.setId(UUID.randomUUID());
    }
    return addressbookEntryMapper.toDto(addressbookRepository.save(addressbookEntryMapper.toEntity(addressbookEntryDto)));
  }

  public List<AddressbookEntryDto> getAllEntries() {
    return addressbookRepository.findAll().stream().map(addressbookEntryMapper::toDto).toList();
  }

  public void deleteAddressbookEntry(UUID id) {
    AddressbookEntry addressbookEntry = addressbookRepository.findById(id).orElse(null);
    if (addressbookEntry != null) {
      addressbookRepository.delete(addressbookEntry);
    } else {
      throw new IllegalArgumentException("AddressbookEntry with ID " + id + " does not exist");
    }
  }

  public AddressbookEntryDto getAddressbookEntry(UUID id) {
    Optional<AddressbookEntry> addressbookEntry = addressbookRepository.findById(id);
    return addressbookEntry.map(addressbookEntryMapper::toDto).orElse(null);
  }

  public boolean updateAddressbookEntry(UUID id, AddressbookEntryDto addressbookEntryDto) {
    if (addressbookRepository.existsById(id)) {
      AddressbookEntry addressbookEntry = addressbookEntryMapper.toEntity(addressbookEntryDto);
      addressbookRepository.save(addressbookEntry);
      return true;
    }
    return false;
  }
}
