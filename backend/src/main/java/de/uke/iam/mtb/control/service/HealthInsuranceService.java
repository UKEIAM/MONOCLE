package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.api.model.HealthInsuranceDto;
import de.uke.iam.mtb.control.models.HealthInsurance;
import de.uke.iam.mtb.control.models.mapper.HealthInsuranceMapper;
import de.uke.iam.mtb.control.repository.HealthInsuranceRepository;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class HealthInsuranceService {

  private final HealthInsuranceRepository healthInsuranceRepository;
  private final HealthInsuranceMapper healthInsuranceMapper;

  public HealthInsuranceService(HealthInsuranceRepository healthInsuranceRepository, HealthInsuranceMapper healthInsuranceMapper) {
    this.healthInsuranceRepository = healthInsuranceRepository;
    this.healthInsuranceMapper = healthInsuranceMapper;
  }

  public void insertListOfEntries(List<HealthInsuranceDto> data) {
    healthInsuranceRepository.deleteAll();

    for (HealthInsuranceDto healthInsuranceDto : data) {
      if (!StringUtils.isBlank(healthInsuranceDto.getNamenszeile1())) {
        addEntry(healthInsuranceDto);
      }
    }
  }

  public HealthInsuranceDto addEntry(HealthInsuranceDto HealthInsuranceDto) {
    return healthInsuranceMapper.toDto(
        healthInsuranceRepository.save(healthInsuranceMapper.toEntity(HealthInsuranceDto)));
  }

  public List<HealthInsuranceDto> getAllEntries() {
    return healthInsuranceRepository.findAll().stream().map(healthInsuranceMapper::toDto).toList();
  }

  public HealthInsuranceDto getHealthInsuranceByID(Long id) {
    return healthInsuranceRepository.findById(id).map(healthInsuranceMapper::toDto).orElse(null);
  }

  public HealthInsurance getHealthInsuranceReference(Long healthInsuranceId) {
    return healthInsuranceRepository.getReferenceById(healthInsuranceId);
  }

}
