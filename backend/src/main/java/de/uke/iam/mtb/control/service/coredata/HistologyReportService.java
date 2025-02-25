package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.HistologyReportDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.coredata.HistologyReport;
import de.uke.iam.mtb.control.models.coredata.Specimen;
import de.uke.iam.mtb.control.models.mapper.coredata.HistologyReportMapper;
import de.uke.iam.mtb.control.repository.coredata.HistologyReportRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class HistologyReportService {

  @PersistenceContext
  private EntityManager entityManager;

  private HistologyReportRepository histologyReportRepository;
  private HistologyReportMapper histologyReportMapper;
  private EpisodeService episodeService;
  private SpecimenService specimenService;

  public HistologyReportService(HistologyReportRepository histologyReportRepository, HistologyReportMapper histologyReportMapper,
      EpisodeService episodeService, SpecimenService specimenService) {

    this.histologyReportRepository = histologyReportRepository;
    this.histologyReportMapper = histologyReportMapper;
    this.episodeService = episodeService;
    this.specimenService = specimenService;

  }

  public HistologyReportDto addHistologyReport(HistologyReportDto histologyReportDto) throws ForeignKeyException {
    if (!episodeService.isEpisodeExist(histologyReportDto.getEpisodeId())) {
      throw new ForeignKeyException("Episode with ID " + histologyReportDto.getEpisodeId() + " does not exist");
    }

    if (!specimenService.isSpecimenExist(histologyReportDto.getSpecimen())) {
      throw new ForeignKeyException("Specimen with ID " + histologyReportDto.getSpecimen() + " does not exist");
    }

    HistologyReport histologyReport = histologyReportMapper.toEntity(histologyReportDto);
    Episode episodeReference = episodeService.getEpisodeReference(histologyReportDto.getEpisodeId());
    Specimen specimenReference = specimenService.getSpecimenReference(histologyReportDto.getSpecimen());
    histologyReport.setEpisode(episodeReference);
    histologyReport.setSpecimen(specimenReference);
    HistologyReport savedHistologyReport = histologyReportRepository.save(histologyReport);

    return histologyReportMapper.toDto(savedHistologyReport);
  }

  public boolean isHistologyReportExist(UUID histologyReportId) {
    return histologyReportRepository.existsById(histologyReportId);
  }

  public HistologyReportDto getHistologyReport(UUID histologyReportId) {
    HistologyReport savedHistologyReport = histologyReportRepository.findById(histologyReportId).orElse(null);
    return histologyReportMapper.toDto(savedHistologyReport);
  }

  public HistologyReport getHistologyReportReference(UUID histologyReportId) {

    return histologyReportRepository.getReferenceById(histologyReportId);
  }

  public List<HistologyReportDto> getAllHistologyReports(UUID episodeId) throws ForeignKeyException {

    if (!episodeService.isEpisodeExist(episodeId)) {
      throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
    }

    List<HistologyReport> savedHistologyReport = histologyReportRepository.getAllByEpisodeId(episodeId);

    return savedHistologyReport.stream().map(histologyReportMapper::toDto).toList();
  }

  public HistologyReportDto updateHistologyReport(UUID histologyReportId, HistologyReportDto histologyReportDto)
      throws ForeignKeyException {

    if (!specimenService.isSpecimenExist(histologyReportDto.getSpecimen())) {
      throw new ForeignKeyException("Specimen with ID " + histologyReportDto.getSpecimen() + " does not exist");
    }

    HistologyReport histologyReport = histologyReportMapper.toEntity(histologyReportDto);
    HistologyReport updatedHistologyReport = histologyReportRepository.save(histologyReport);

    return histologyReportMapper.toDto(updatedHistologyReport);
  }

  public Boolean isHistologyReportReferenced(UUID histologyReportId) {
    // Fix: Since it is not possible to access an intermediate table using Hibernate without creating a model,
    // a conventional SQL statement is executed here. This can also be replaced by jOOQ after the refactoring.
    return (Boolean) entityManager.createNativeQuery("SELECT EXISTS (SELECT 1 FROM diagnose_histology_report WHERE histology_report_id = '"+histologyReportId+"') AS exists;").getSingleResult();
  }

  public void deleteHistologyReport(UUID histologyReportId) {

    if (isHistologyReportReferenced(histologyReportId)) {
      throw new IllegalStateException("The Histology Report cannot be deleted as it is referenced in other tables.");
    }
    
    histologyReportRepository.deleteById(histologyReportId);
  }

}
