package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.TherapyRecommendationDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.coredata.Diagnose;
import de.uke.iam.mtb.control.models.coredata.NgsReport;
import de.uke.iam.mtb.control.models.coredata.TherapyRecommendation;
import de.uke.iam.mtb.control.models.mapper.coredata.TherapyRecommendationMapper;
import de.uke.iam.mtb.control.repository.coredata.ClaimRepository;
import de.uke.iam.mtb.control.repository.coredata.MolecularTherapyRepository;
import de.uke.iam.mtb.control.repository.coredata.TherapyRecommendationRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TherapyRecommendationService {

  @PersistenceContext
  private EntityManager entityManager;

  private TherapyRecommendationRepository therapyRecommendationRepository;
  private TherapyRecommendationMapper therapyRecommendationMapper;
  private EpisodeService episodeService;
  private DiagnoseService diagnoseService;
  private NgsReportService ngsReportService;

  private ClaimRepository claimRepository;
  private MolecularTherapyRepository molecularTherapyRepository;

  public TherapyRecommendationService(TherapyRecommendationRepository therapyRecommendationRepository, ClaimRepository claimRepository, MolecularTherapyRepository molecularTherapyRepository, 
      TherapyRecommendationMapper therapyRecommendationMapper,
      EpisodeService episodeService, DiagnoseService diagnoseService, NgsReportService ngsReportService) {

    this.therapyRecommendationRepository = therapyRecommendationRepository;
    this.therapyRecommendationMapper = therapyRecommendationMapper;
    this.episodeService = episodeService;
    this.diagnoseService = diagnoseService;
    this.ngsReportService = ngsReportService;
    this.claimRepository = claimRepository;
    this.molecularTherapyRepository = molecularTherapyRepository;

  }

  public TherapyRecommendationDto addTherapyRecommendation(TherapyRecommendationDto therapyRecommendationDto) throws ForeignKeyException {
    if (!episodeService.isEpisodeExist(therapyRecommendationDto.getEpisodeId())) {
      throw new ForeignKeyException("Episode with ID " + therapyRecommendationDto.getEpisodeId() + " does not exist");
    }

    if (!diagnoseService.isDiagnoseExist(therapyRecommendationDto.getDiagnosis())) {
      throw new ForeignKeyException("Diagnose with ID " + therapyRecommendationDto.getDiagnosis() + " does not exist");
    }

    if (therapyRecommendationDto.getNgsReport() != null && !ngsReportService.isNgsReportExist(therapyRecommendationDto.getNgsReport())) {
        throw new ForeignKeyException("NgsReport with ID " + therapyRecommendationDto.getNgsReport() + " does not exist");
    }

    TherapyRecommendation therapyRecommendation = therapyRecommendationMapper.toEntity(therapyRecommendationDto);

    // Get references to Episode and Diagnose
    Episode episodeReference = episodeService.getEpisodeReference(therapyRecommendationDto.getEpisodeId());
    Diagnose diagnoseReference = diagnoseService.getDiagnoseReference(therapyRecommendationDto.getDiagnosis());

    // Set Episode and Diagnose references
    therapyRecommendation.setEpisode(episodeReference);
    therapyRecommendation.setDiagnosis(diagnoseReference);

    // If NgsReport is provided, get reference and set it
    if (therapyRecommendationDto.getNgsReport() != null ) {
      NgsReport ngsReportReference = ngsReportService.getNgsReportReference(therapyRecommendationDto.getNgsReport());
      therapyRecommendation.setNgsReport(ngsReportReference);
    } else {
      therapyRecommendation.setNgsReport(null);
      therapyRecommendation.setSupportingVariants(null);
    }

    // Save the TherapyRecommendation entity
    TherapyRecommendation savedTherapyRecommendation = therapyRecommendationRepository.save(therapyRecommendation);

    return therapyRecommendationMapper.toDto(savedTherapyRecommendation);
  }

  public boolean isTherapyRecommendationExist(UUID therapyRecommendationId) {
    return therapyRecommendationRepository.existsById(therapyRecommendationId);
  }

  public TherapyRecommendationDto getTherapyRecommendation(UUID therapyRecommendationId) {
    TherapyRecommendation savedTherapyRecommendation = therapyRecommendationRepository.findById(therapyRecommendationId).orElse(null);
    return therapyRecommendationMapper.toDto(savedTherapyRecommendation);
  }

  public TherapyRecommendation getTherapyRecommendationReference(UUID therapyRecommendationId) {

    return therapyRecommendationRepository.getReferenceById(therapyRecommendationId);
  }

  public List<TherapyRecommendationDto> getAllTherapyRecommendations(UUID episodeId) throws ForeignKeyException {

    if (!episodeService.isEpisodeExist(episodeId)) {
      throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
    }

    List<TherapyRecommendation> savedTherapyRecommendation = therapyRecommendationRepository.getAllByEpisodeId(episodeId);

    return savedTherapyRecommendation.stream().map(therapyRecommendationMapper::toDto).toList();
  }

  public TherapyRecommendationDto updateTherapyRecommendation(UUID therapyRecommendationId,
      TherapyRecommendationDto therapyRecommendationDto)
      throws ForeignKeyException {

    if (!diagnoseService.isDiagnoseExist(therapyRecommendationDto.getDiagnosis())) {
      throw new ForeignKeyException("Diagnose with ID " + therapyRecommendationDto.getDiagnosis() + " does not exist");
    }

    if (therapyRecommendationDto.getNgsReport() != null && !ngsReportService.isNgsReportExist(therapyRecommendationDto.getNgsReport())) {
      throw new ForeignKeyException("NgsReport with ID " + therapyRecommendationDto.getNgsReport() + " does not exist");
    }

    TherapyRecommendation therapyRecommendation = therapyRecommendationMapper.toEntity(therapyRecommendationDto);

    // If NgsReport is provided, get reference and set it
    if (therapyRecommendationDto.getNgsReport() != null ) {
      NgsReport ngsReportReference = ngsReportService.getNgsReportReference(therapyRecommendationDto.getNgsReport());
      therapyRecommendation.setNgsReport(ngsReportReference);
    } else {
      therapyRecommendation.setNgsReport(null);
      therapyRecommendation.setSupportingVariants(null);
    }

    TherapyRecommendation updatedTherapyRecommendation = therapyRecommendationRepository.save(therapyRecommendation);

    return therapyRecommendationMapper.toDto(updatedTherapyRecommendation);
  }

  public Boolean isTherapyRecommendationReferenced(UUID therapyRecommendationId) {
    // Fix: Since it is not possible to access an intermediate table using Hibernate without creating a model,
    // a conventional SQL statement is executed here. This can also be replaced by jOOQ after the refactoring.
    return (Boolean) entityManager.createNativeQuery("SELECT EXISTS (SELECT 1 FROM care_plan_therapy_recommendation WHERE therapy_recommendation_id = '"+therapyRecommendationId+"') AS exists;").getSingleResult();
  }

  public void deleteTherapyRecommendation(UUID therapyRecommendationId) {

    if (
      isTherapyRecommendationReferenced(therapyRecommendationId) ||
      claimRepository.existsByTherapyRecommendationId(therapyRecommendationId) ||
      molecularTherapyRepository.existsByTherapyRecommendationId(therapyRecommendationId)
      )
      {
        throw new IllegalStateException("The Therapy Recommendation cannot be deleted as it is referenced in other tables.");
      }

    therapyRecommendationRepository.deleteById(therapyRecommendationId);

  }

}
