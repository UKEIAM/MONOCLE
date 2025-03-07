package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.MolecularPathologyFindingDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.coredata.MolecularPathologyFinding;
import de.uke.iam.mtb.control.models.coredata.Specimen;
import de.uke.iam.mtb.control.models.mapper.coredata.MolecularPathologyFindingMapper;
import de.uke.iam.mtb.control.repository.coredata.MolecularPathologyFindingRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MolecularPathologyFindingService {

  private final MolecularPathologyFindingRepository molecularPathologyFindingRepository;
  private final MolecularPathologyFindingMapper molecularPathologyFindingMapper;
  private final EpisodeService episodeService;
  private final SpecimenService specimenService;

  public MolecularPathologyFindingService(MolecularPathologyFindingRepository molecularPathologyFindingRepository,
      MolecularPathologyFindingMapper molecularPathologyFindingMapper, EpisodeService episodeService, SpecimenService specimenService) {

    this.molecularPathologyFindingRepository = molecularPathologyFindingRepository;
    this.molecularPathologyFindingMapper = molecularPathologyFindingMapper;
    this.episodeService = episodeService;
    this.specimenService = specimenService;

  }

  public MolecularPathologyFindingDto addMolecularPathologyFinding(MolecularPathologyFindingDto molecularPathologyFindingDto)
      throws ForeignKeyException {
    if (!episodeService.isEpisodeExist(molecularPathologyFindingDto.getEpisodeId())) {
      throw new ForeignKeyException("Episode with ID " + molecularPathologyFindingDto.getEpisodeId() + " does not exist");
    }

    if (!specimenService.isSpecimenExist(molecularPathologyFindingDto.getSpecimen())) {
      throw new ForeignKeyException("Specimen with ID " + molecularPathologyFindingDto.getSpecimen() + " does not exist");
    }

    MolecularPathologyFinding molecularPathologyFinding = molecularPathologyFindingMapper.toEntity(molecularPathologyFindingDto);
    Episode episodeReference = episodeService.getEpisodeReference(molecularPathologyFindingDto.getEpisodeId());
    Specimen specimenReference = specimenService.getSpecimenReference(molecularPathologyFindingDto.getSpecimen());
    molecularPathologyFinding.setEpisode(episodeReference);
    molecularPathologyFinding.setSpecimen(specimenReference);
    MolecularPathologyFinding savedMolecularPathologyFinding = molecularPathologyFindingRepository.save(molecularPathologyFinding);

    return molecularPathologyFindingMapper.toDto(savedMolecularPathologyFinding);
  }

  public boolean isMolecularPathologyFindingExist(UUID molecularPathologyFindingId) {
    return molecularPathologyFindingRepository.existsById(molecularPathologyFindingId);
  }

  public MolecularPathologyFindingDto getMolecularPathologyFinding(UUID molecularPathologyFindingId) {
    MolecularPathologyFinding savedMolecularPathologyFinding = molecularPathologyFindingRepository.findById(molecularPathologyFindingId)
        .orElse(null);
    return molecularPathologyFindingMapper.toDto(savedMolecularPathologyFinding);
  }


  public MolecularPathologyFinding getMolecularPathologyFindingReference(UUID molecularPathologyFindingId) {

    return molecularPathologyFindingRepository.getReferenceById(molecularPathologyFindingId);
  }

  public List<MolecularPathologyFindingDto> getAllMolecularPathologyFindings(UUID episodeId) throws ForeignKeyException {

    if (!episodeService.isEpisodeExist(episodeId)) {
      throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
    }

    List<MolecularPathologyFinding> savedMolecularPathologyFindings = molecularPathologyFindingRepository.getAllByEpisodeId(episodeId);

    return savedMolecularPathologyFindings.stream().map(molecularPathologyFindingMapper::toDto).toList();
  }

  public MolecularPathologyFindingDto updateMolecularPathologyFinding(UUID molecularPathologyFindingId,
      MolecularPathologyFindingDto molecularPathologyFindingDto) throws ForeignKeyException {

    if (!specimenService.isSpecimenExist(molecularPathologyFindingDto.getSpecimen())) {
      throw new ForeignKeyException("Specimen with ID " + molecularPathologyFindingDto.getSpecimen() + " does not exist");
    }

    MolecularPathologyFinding molecularPathologyFinding = molecularPathologyFindingMapper.toEntity(molecularPathologyFindingDto);
    MolecularPathologyFinding updatedMolecularPathologyFinding = molecularPathologyFindingRepository.save(molecularPathologyFinding);

    return molecularPathologyFindingMapper.toDto(updatedMolecularPathologyFinding);
  }

  public void deleteMolecularPathologyFinding(UUID molecularPathologyFindingId) {

    molecularPathologyFindingRepository.deleteById(molecularPathologyFindingId);

  }

}
