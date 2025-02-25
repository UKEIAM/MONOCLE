package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.MolecularTherapyDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.coredata.MolecularTherapy;
import de.uke.iam.mtb.control.models.coredata.TherapyRecommendation;
import de.uke.iam.mtb.control.models.mapper.coredata.MolecularTherapyMapper;
import de.uke.iam.mtb.control.repository.coredata.MolecularTherapyRepository;
import de.uke.iam.mtb.control.repository.coredata.MolecularTherapyResponseRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MolecularTherapyService {

  private MolecularTherapyRepository molecularTherapyRepository;
  private MolecularTherapyMapper molecularTherapyMapper;
  private EpisodeService episodeService;
  private TherapyRecommendationService therapyRecommendationService;

  private MolecularTherapyResponseRepository molecularTherapyResponseRepository;

  public MolecularTherapyService(MolecularTherapyRepository molecularTherapyRepository, MolecularTherapyResponseRepository molecularTherapyResponseRepository, MolecularTherapyMapper molecularTherapyMapper,
      EpisodeService episodeService, TherapyRecommendationService therapyRecommendationService) {

    this.molecularTherapyRepository = molecularTherapyRepository;
    this.molecularTherapyMapper = molecularTherapyMapper;
    this.episodeService = episodeService;
    this.therapyRecommendationService = therapyRecommendationService;
    this.molecularTherapyResponseRepository = molecularTherapyResponseRepository;

  }

  public MolecularTherapyDto addMolecularTherapy(MolecularTherapyDto molecularTherapyDto) throws ForeignKeyException {
    if (!episodeService.isEpisodeExist(molecularTherapyDto.getEpisodeId())) {
      throw new ForeignKeyException("Episode with ID " + molecularTherapyDto.getEpisodeId() + " does not exist");
    }

    if (!therapyRecommendationService.isTherapyRecommendationExist(molecularTherapyDto.getBasedOn())) {
      throw new ForeignKeyException("TherapyRecommendation with ID " + molecularTherapyDto.getBasedOn() + " does not exist");
    }

    MolecularTherapy molecularTherapy = molecularTherapyMapper.toEntity(molecularTherapyDto);
    Episode episodeReference = episodeService.getEpisodeReference(molecularTherapyDto.getEpisodeId());
    TherapyRecommendation therapyRecommendationReference = therapyRecommendationService.getTherapyRecommendationReference(
        molecularTherapyDto.getBasedOn());
    molecularTherapy.setEpisode(episodeReference);
    molecularTherapy.setTherapyRecommendation(therapyRecommendationReference);
    MolecularTherapy savedMolecularTherapy = molecularTherapyRepository.save(molecularTherapy);

    return molecularTherapyMapper.toDto(savedMolecularTherapy);
  }

  public boolean isMolecularTherapyExist(UUID molecularTherapyId) {
    return molecularTherapyRepository.existsById(molecularTherapyId);
  }

  public MolecularTherapyDto getMolecularTherapy(UUID molecularTherapyId) {
    MolecularTherapy savedMolecularTherapy = molecularTherapyRepository.findById(molecularTherapyId).orElse(null);
    return molecularTherapyMapper.toDto(savedMolecularTherapy);
  }


  public MolecularTherapy getMolecularTherapyReference(UUID molecularTherapyId) {

    return molecularTherapyRepository.getReferenceById(molecularTherapyId);
  }

  public List<MolecularTherapyDto> getAllMolecularTherapies(UUID episodeId) throws ForeignKeyException {

    if (!episodeService.isEpisodeExist(episodeId)) {
      throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
    }

    List<MolecularTherapy> savedMolecularTherapy = molecularTherapyRepository.getAllByEpisodeId(episodeId);

    return savedMolecularTherapy.stream().map(molecularTherapyMapper::toDto).toList();
  }

  public MolecularTherapyDto updateMolecularTherapy(UUID molecularTherapyId, MolecularTherapyDto molecularTherapyDto)
      throws ForeignKeyException {

    if (!therapyRecommendationService.isTherapyRecommendationExist(molecularTherapyDto.getBasedOn())) {
      throw new ForeignKeyException("TherapyRecommendation with ID " + molecularTherapyDto.getBasedOn() + " does not exist");
    }

    MolecularTherapy molecularTherapy = molecularTherapyMapper.toEntity(molecularTherapyDto);
    MolecularTherapy updatedMolecularTherapy = molecularTherapyRepository.save(molecularTherapy);

    return molecularTherapyMapper.toDto(updatedMolecularTherapy);
  }

  public void deleteMolecularTherapy(UUID molecularTherapyId) {

    if (molecularTherapyResponseRepository.existsByTherapyId(molecularTherapyId)) {
      throw new IllegalStateException("The Molecular Therapy cannot be deleted as it is referenced in other tables.");
    }

    molecularTherapyRepository.deleteById(molecularTherapyId);

  }

}
