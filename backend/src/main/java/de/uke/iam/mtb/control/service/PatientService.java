package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.api.model.PatientDto;
import de.uke.iam.mtb.control.exception.DuplicateSoarianIdException;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.HealthInsurance;
import de.uke.iam.mtb.control.models.Patient;
import de.uke.iam.mtb.control.models.mapper.PatientMapper;
import de.uke.iam.mtb.control.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PatientService {

  private final PatientRepository patientRepository;
  private final HealthInsuranceService healthInsuranceService;
  private final EpisodeService episodeService;
  private final PatientMapper patientMapper;

  public PatientService(PatientRepository patientRepository, HealthInsuranceService healthInsuranceService, EpisodeService episodeService
      , PatientMapper patientMapper) {
    this.patientRepository = patientRepository;
    this.healthInsuranceService = healthInsuranceService;
    this.episodeService = episodeService;
    this.patientMapper = patientMapper;
  }

  public PatientDto getPatient(UUID id) {
    Patient savedPatient = patientRepository.findById(id).orElse(null);
    if (savedPatient != null) {
      Patient filteredPatient = filterPatientByLatestEpisode(savedPatient);
      return patientMapper.toDto(filteredPatient);
    }
    return null;
  }

  public Patient getPatientReference(UUID id) {
    return patientRepository.getReferenceById(id);
  }

  public List<PatientDto> getAllPatients() {
    return patientRepository.findAll().stream().map(this::filterPatientByLatestEpisode).map(patientMapper::toDto).toList();
  }

  public Patient filterPatientByLatestEpisode(Patient patient) {
    Episode latestEpisode = patient.getEpisodes().stream()
        .max(Comparator.comparing(Episode::getCreatedAt)).orElse(null);
    if (latestEpisode != null) {
      patient.setEpisodes(List.of(latestEpisode));
    }
    return patient;
  }

  @Deprecated
  // too dangerous outside development
  public void deleteAllPatients() {
    patientRepository.deleteAll();
  }

  public PatientDto addPatient(PatientDto patientDto) throws ForeignKeyException, DuplicateSoarianIdException {
    if (patientRepository.existsBySoarianId(patientDto.getSoarianId())) {
      throw new DuplicateSoarianIdException("Soarian ID already exists");
    }
    HealthInsurance healthInsuranceReference = null;
    if (patientDto.getHealthInsurance() != null) {
      try {
        healthInsuranceReference = healthInsuranceService.getHealthInsuranceReference(patientDto.getHealthInsurance());
        String x = healthInsuranceReference.toString();
      } catch (EntityNotFoundException e) {
        throw new ForeignKeyException("HealthInsurance with ID " + patientDto.getHealthInsurance() + " does not exist");
      }
    }
    Patient patient = patientMapper.toEntity(patientDto);
    patient.setHealthInsurance(healthInsuranceReference);
    Patient savedPatient = patientRepository.save(patient);
    // create a new episode for the patient
    episodeService.addEpisode(savedPatient.getId(), patientDto.getWorkflowId(), true);
    return patientMapper.toDto(savedPatient);
  }

  public PatientDto updatePatient(UUID patientId, PatientDto updatedPatientDto) throws DuplicateSoarianIdException, ForeignKeyException {
    if (isSoarianIdAssignedToAnotherPatient(patientId, updatedPatientDto.getSoarianId())) {
      throw new DuplicateSoarianIdException("Soarian ID already assigned to another patient");
    }

    HealthInsurance healthInsuranceReference = null;
    if (updatedPatientDto.getHealthInsurance() != null) {
      try {
        healthInsuranceReference = healthInsuranceService.getHealthInsuranceReference(updatedPatientDto.getHealthInsurance());
        String x = healthInsuranceReference.toString();
      } catch (EntityNotFoundException e) {
        throw new ForeignKeyException("HealthInsurance with ID " + updatedPatientDto.getHealthInsurance() + " does not exist");
      }
    }
    Patient updatedPatient = patientMapper.toEntity(updatedPatientDto);
    updatedPatient.setHealthInsurance(healthInsuranceReference);
    return patientMapper.toDto(patientRepository.save(updatedPatient));
  }

  private boolean isSoarianIdAssignedToAnotherPatient(UUID patientId, String soarianID) {
    Optional<Patient> patient = patientRepository.getBySoarianId(soarianID);
    return patient.map(value -> !value.getId().equals(patientId)).orElse(true);
  }

  public boolean isPatientExist(UUID id) {
    return patientRepository.existsById(id);
  }
}
