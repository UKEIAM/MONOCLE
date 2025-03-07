package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.api.model.RequirementDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.Requirement;
import de.uke.iam.mtb.control.models.mapper.EpisodeMapper;
import de.uke.iam.mtb.control.models.mapper.RequirementMapper;
import de.uke.iam.mtb.control.repository.RequirementRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RequirementService {

  private final RequirementRepository requirementRepository;
  private final EpisodeService episodeService;

  private final RequirementMapper requirementMapper;

  private final EpisodeMapper episodeMapper;

  public RequirementService(RequirementRepository requirementRepository,
      EpisodeService episodeService, RequirementMapper requirementMapper, EpisodeMapper episodeMapper) {
    this.requirementRepository = requirementRepository;
    this.episodeService = episodeService;
    this.requirementMapper = requirementMapper;
    this.episodeMapper = episodeMapper;
  }

  public RequirementDto addRequirement(RequirementDto requirementDto) throws ForeignKeyException {
    if (!episodeService.isEpisodeExist(requirementDto.getEpisodeId()) || requirementDto.getEpisodeId() == null) {
      throw new ForeignKeyException("Episode with ID " + requirementDto.getEpisodeId() + " does not exist");
    }
    Episode episodeReference = episodeService.getEpisodeReference(requirementDto.getEpisodeId());
    Requirement requirement = requirementMapper.toEntity(requirementDto);
    requirement.setEpisode(episodeReference);
    Requirement savedRequirement = requirementRepository.save(requirement);
    // update episode with new requirement
    episodeReference.setRequirement(savedRequirement);
    episodeService.updateEpisode(episodeReference.getId(), episodeMapper.toDto(episodeReference));
    return requirementMapper.toDto(savedRequirement);
  }

  public RequirementDto updateRequirement(RequirementDto requirementDto) throws ForeignKeyException {
    Requirement savedRequirement = requirementRepository.findById(requirementDto.getId()).orElse(null);
    if (savedRequirement != null) {
      if (!savedRequirement.getEpisode().getId().equals(requirementDto.getEpisodeId())) {
        throw new ForeignKeyException("Given EpisodeId is not the same as the one in the savedRequirement");
      }
      Requirement updatedRequirement = requirementRepository.save(
          requirementMapper.toEntity(requirementDto));
      return requirementMapper.toDto(updatedRequirement);
    }
    return null;
  }

  public RequirementDto getRequirement(UUID id) {
    Requirement requirement = requirementRepository.findById(id).orElse(null);
    return requirementMapper.toDto(requirement);
  }

  public Requirement getRequirementReference(UUID id) {
    return requirementRepository.getReferenceById(id);
  }

  public boolean isRequirementExist(UUID id) {
    return requirementRepository.findById(id).orElse(null) != null;
  }

  public boolean isRequirementPresentForEpisode(UUID episodeId) {
    return requirementRepository.existsByEpisodeId(episodeId);
  }
}
