package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.api.model.StepInfoDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.Step;
import de.uke.iam.mtb.control.models.StepInfo;
import de.uke.iam.mtb.control.models.StepInfoId;
import de.uke.iam.mtb.control.models.enums.StepStatus;
import de.uke.iam.mtb.control.models.mapper.StepInfoMapper;
import de.uke.iam.mtb.control.repository.EpisodeRepository;
import de.uke.iam.mtb.control.repository.StepInfoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class StepInfoService {

  private final EpisodeRepository episodeRepository;
  private final StepInfoMapper stepInfoMapper;
  StepInfoRepository stepInfoRepository;

  public StepInfoService(StepInfoRepository stepInfoRepository, EpisodeRepository episodeRepository, StepInfoMapper stepInfoMapper) {
    this.stepInfoRepository = stepInfoRepository;
    this.episodeRepository = episodeRepository;
    this.stepInfoMapper = stepInfoMapper;
  }

  public void addStepInfo(Step step, Episode episode, StepStatus stepStatus) {
    StepInfoId stepInfoId = new StepInfoId(step.getId(), episode.getId());
    StepInfo stepInfo = new StepInfo(stepInfoId, step, episode, stepStatus);
    stepInfoRepository.save(stepInfo);
  }

  public StepInfo getStepInfo(Integer stepId, UUID episodeId) {
    StepInfoId stepInfoId = new StepInfoId(stepId, episodeId);
    return stepInfoRepository.findById(stepInfoId).orElse(null);
  }

  public List<StepInfoDto> getStepsInfoByEpisodeId(UUID episodeId) {
    Episode episode = episodeRepository.findById(episodeId).orElse(null);
    if (episode == null) {
      return null;
    }
    List<StepInfoDto> stepsInfo = new ArrayList<>();
    episode.getWorkflow().getSteps().forEach(step -> stepsInfo.add(stepInfoMapper.toDto(getStepInfo(step.getId(), episode.getId()))));
    return stepsInfo;
  }

  public void updateStepsInfo(UUID episodeId, List<StepInfoDto> stepInfosDto) throws ForeignKeyException {
    Episode episode = episodeRepository.findById(episodeId).orElse(null);
    if (episode == null) {
      throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
    }
    stepInfosDto.forEach(stepInfoDto -> {
      StepInfo updatedStepInfo = stepInfoMapper.toEntity(stepInfoDto);
      StepInfo savedStepInfo = getStepInfo(stepInfoDto.getStepId(), stepInfoDto.getEpisodeId());
      savedStepInfo.setStepStatus(updatedStepInfo.getStepStatus());
      stepInfoRepository.save(savedStepInfo);
    });
  }
}
