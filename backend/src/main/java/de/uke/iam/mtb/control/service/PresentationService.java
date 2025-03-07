package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.api.model.PresentationDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.Presentation;
import de.uke.iam.mtb.control.models.mapper.EpisodeMapper;
import de.uke.iam.mtb.control.models.mapper.PresentationMapper;
import de.uke.iam.mtb.control.repository.PresentationRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PresentationService {

  private final PresentationRepository presentationRepository;
  private final EpisodeService episodeService;
  private final EpisodeMapper episodeMapper;
  private final PresentationMapper presentationMapper;


  public PresentationService(PresentationRepository presentationRepository, EpisodeService episodeService, EpisodeMapper episodeMapper,
      PresentationMapper presentationMapper) {
    this.presentationRepository = presentationRepository;
    this.episodeService = episodeService;
    this.episodeMapper = episodeMapper;
    this.presentationMapper = presentationMapper;
  }

  public PresentationDto addPresentation(UUID episodeId, PresentationDto presentationDto) throws ForeignKeyException {
    if (!episodeService.isEpisodeExist(episodeId)) {
      throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
    }
    Presentation presentation = new Presentation();
    presentation.setDateOfPresentation(presentationDto.getDateOfPresentation());
    Episode episode = episodeMapper.toEntity(episodeService.getEpisode(episodeId));
    presentation.setEpisode(episode);
    return presentationMapper.toDto(presentationRepository.save(presentation));

  }

  public boolean isPresentationExist(UUID presentationId) {
    return presentationRepository.existsById(presentationId);
  }

  public boolean isPresentationWithDateExist(UUID episodeId, PresentationDto presentationDto) {
    return presentationRepository.existsByEpisodeIdAndDateOfPresentation(episodeId, presentationDto.getDateOfPresentation());
  }

  public List<PresentationDto> getAllPresentationsByEpisodeId(UUID episodeId) throws ForeignKeyException {

    if (!episodeService.isEpisodeExist(episodeId)) {
      throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
    }
    if (presentationRepository.findAllByEpisodeId(episodeId).isEmpty()) {
      return null;
    }
    return presentationRepository.findAllByEpisodeId(episodeId).stream().map(presentationMapper::toDto).toList();
  }

  public PresentationDto getPresentation(UUID presentationId) {

    Presentation savedPresentation = presentationRepository.findById(presentationId).orElse(null);

    return presentationMapper.toDto(savedPresentation);
  }

  public PresentationDto updatePresentation(UUID presentationId, PresentationDto presentationDto) {

    Presentation savedPresentation = presentationRepository.findById(presentationId).orElse(null);
    PresentationDto updatedPresentationDto = null;
    if (savedPresentation != null) {
      savedPresentation.setDateOfPresentation(presentationDto.getDateOfPresentation());
      updatedPresentationDto = presentationMapper.toDto(presentationRepository.save(savedPresentation));
    }
    return updatedPresentationDto;
  }

  public void deletePresentation(UUID presentationId) {
    presentationRepository.deleteById(presentationId);
  }


}
