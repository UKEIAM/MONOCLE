package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.api.model.EpisodeDto;
import de.uke.iam.mtb.api.model.StepInfoDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.Patient;
import de.uke.iam.mtb.control.models.Requirement;
import de.uke.iam.mtb.control.models.Workflow;
import de.uke.iam.mtb.control.models.enums.StepStatus;
import de.uke.iam.mtb.control.models.mapper.CloningMapper;
import de.uke.iam.mtb.control.models.mapper.EpisodeMapper;
import de.uke.iam.mtb.control.repository.EpisodeRepository;
import de.uke.iam.mtb.control.repository.PatientRepository;
import de.uke.iam.mtb.control.repository.RequirementRepository;
import de.uke.iam.mtb.control.repository.WorkflowRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class EpisodeService {

  private final EpisodeRepository episodeRepository;
  private final WorkflowRepository workflowRepository;
  private final PatientRepository patientRepository;
  private final StepInfoService stepInfoService;
  private final EpisodeMapper episodeMapper;
  private final RequirementRepository requirementRepository;
  private final CoreDataCloneService coreDataCloneService;

  public EpisodeService(EpisodeRepository episodeRepository, WorkflowRepository workflowRepository, PatientRepository patientRepository,
      StepInfoService stepInfoService, EpisodeMapper episodeMapper, RequirementRepository requirementRepository,
      CoreDataCloneService coreDataCloneService) {

    this.episodeRepository = episodeRepository;
    this.workflowRepository = workflowRepository;
    this.patientRepository = patientRepository;
    this.stepInfoService = stepInfoService;
    this.episodeMapper = episodeMapper;
    this.requirementRepository = requirementRepository;
    this.coreDataCloneService = coreDataCloneService;

  }

  // @Transactional means that the method is executed in a transaction, so that the whole method is executed or nothing is executed
  @Transactional
  public EpisodeDto addEpisode(UUID patientId, Integer workflowId, boolean isFirstEpisode) throws ForeignKeyException {
    // check if the patient and workflow exist
    try {
      Patient patient = patientRepository.getReferenceById(patientId);
      Workflow workflow = workflowRepository.getReferenceById(workflowId);
      // The following lines will throw an EntityNotFoundException if the patient or workflow does not exist
      patient.getId();
      workflow.getId();

      Episode newEpisode = new Episode();
      Episode latestEpisode = null;
      // if the episode is not the first one then get the latest episode of the patient
      if (!isFirstEpisode) {
        latestEpisode = patient.getEpisodes().stream()
            .max(Comparator.comparing(Episode::getCreatedAt)).orElse(null);
      }
      newEpisode.setPatient(patient);
      newEpisode.setWorkflow(workflow);
      Episode savedEpisode = episodeRepository.save(newEpisode);

      if (!isFirstEpisode && latestEpisode != null) {
        cloneEpisodeData(latestEpisode, newEpisode);
      }

      // after saving, set the stepInfos for the episode in the database
      setInitialStepInfos(savedEpisode);
      // get StepInfos from database to set them for a response
      List<StepInfoDto> stepsInfo = stepInfoService.getStepsInfoByEpisodeId(savedEpisode.getId());

      EpisodeDto savedEpisodeDto = episodeMapper.toDto(savedEpisode);
      savedEpisodeDto.setStepInfo(stepsInfo);
      return savedEpisodeDto;

    } catch (EntityNotFoundException e) {
      throw new ForeignKeyException("Patient or Workflow Entity does not exist.\n" + e.getMessage());
    }
  }

  public void cloneEpisodeData(Episode oldEpisode, Episode newEpisode) {

    newEpisode.setReport(oldEpisode.getReport());
    newEpisode.setDecision(oldEpisode.getDecision());
    // copy the requirement of the latest episode to the new episode
    Requirement oldRequirement = requirementRepository.findByEpisodeId(oldEpisode.getId());
    if (oldRequirement != null) {
      Requirement clonedRequirement = CloningMapper.INSTANCE.clone(oldRequirement);
      clonedRequirement.setEpisode(episodeRepository.getReferenceById(newEpisode.getId()));
      Requirement newRequirement = requirementRepository.save(clonedRequirement);
      newEpisode.setRequirement(newRequirement);
    }
    // By Creating a new Episode , copy the old core data of the patient to the new episode using CoreDataCloneService
   coreDataCloneService.cloneCoreData(oldEpisode.getId(), newEpisode);
  }

  private void setInitialStepInfos(Episode episode) {
    episode.getWorkflow().getSteps().forEach(step -> stepInfoService.addStepInfo(step, episode, StepStatus.INCOMPLETE));
  }

  public EpisodeDto getEpisode(UUID id) {
    Episode savedEpisode = episodeRepository.findById(id).orElse(null);
    List<StepInfoDto> stepsInfo = stepInfoService.getStepsInfoByEpisodeId(id);
    EpisodeDto episodeDto = episodeMapper.toDto(savedEpisode);
    episodeDto.setStepInfo(stepsInfo);
    return episodeMapper.toDto(savedEpisode);
  }

  public Episode getEpisodeReference(UUID id) {

    return episodeRepository.getReferenceById(id);

  }

  public EpisodeDto updateEpisode(UUID id, EpisodeDto episodeDto) throws ForeignKeyException {
    Episode savedEpisode = episodeRepository.findById(id).orElse(null);
    if (savedEpisode != null) {
      // Check if the given PatientId is not the same as the one in the savedEpisode
      if (!savedEpisode.getPatient().getId().equals(episodeDto.getPatientId())) {
        throw new ForeignKeyException("Given PatientId is not the same as the one in the savedEpisode");
      }
      if (!savedEpisode.getWorkflow().getId().equals(episodeDto.getWorkflowId())) {
        throw new ForeignKeyException("Given WorkflowId is not the same as the one in the savedEpisode");
      }

      Episode episode = episodeMapper.toEntity(episodeDto);
      // set the requirement to the savedEpisode's requirement if the requirementId is not given
      if (episodeDto.getRequirementId() == null) {
        if (savedEpisode.getRequirement() != null) {
          episode.setRequirement(savedEpisode.getRequirement());
        } else {
          episode.setRequirement(null);
        }
      }

      Episode updatedEpisode = episodeRepository.save(episode);
      List<StepInfoDto> stepsInfo = stepInfoService.getStepsInfoByEpisodeId(updatedEpisode.getId());
      EpisodeDto updatedEpisodeDto = episodeMapper.toDto(updatedEpisode);
      updatedEpisodeDto.setStepInfo(stepsInfo);
      return updatedEpisodeDto;
    }
    return null;
  }

  public boolean isEpisodeExist(UUID id) {
    return episodeRepository.existsById(id);
  }

}

