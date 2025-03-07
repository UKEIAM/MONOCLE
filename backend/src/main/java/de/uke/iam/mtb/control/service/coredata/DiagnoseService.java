package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.DiagnoseDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.coredata.Diagnose;
import de.uke.iam.mtb.control.models.mapper.coredata.DiagnoseMapper;
import de.uke.iam.mtb.control.repository.coredata.CarePlanRepository;
import de.uke.iam.mtb.control.repository.coredata.DiagnoseRepository;
import de.uke.iam.mtb.control.repository.coredata.GuidelineTherapyRepository;
import de.uke.iam.mtb.control.repository.coredata.StudyInclusionRequestRepository;
import de.uke.iam.mtb.control.repository.coredata.TherapyRecommendationRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiagnoseService {

  private final DiagnoseRepository diagnoseRepository;
  private final DiagnoseMapper diagnoseMapper;
  private final EpisodeService episodeService;
  private final HistologyReportService histologyReportService;
  
  private final CarePlanRepository carePlanRepository;
  private final GuidelineTherapyRepository guidelineTherapyRepository;
  private final StudyInclusionRequestRepository studyInclusionRequestRepository;
  private final TherapyRecommendationRepository therapyRecommendationRepository;

  public DiagnoseService(DiagnoseRepository diagnoseRepository, CarePlanRepository carePlanRepository, GuidelineTherapyRepository guidelineTherapyRepository, StudyInclusionRequestRepository studyInclusionRequestRepository, TherapyRecommendationRepository therapyRecommendationRepository, DiagnoseMapper diagnoseMapper, EpisodeService episodeService,
      HistologyReportService histologyReportService) {
    this.diagnoseRepository = diagnoseRepository;
    this.diagnoseMapper = diagnoseMapper;
    this.episodeService = episodeService;
    this.histologyReportService = histologyReportService;

    this.carePlanRepository = carePlanRepository;
    this.guidelineTherapyRepository = guidelineTherapyRepository;
    this.studyInclusionRequestRepository = studyInclusionRequestRepository;
    this.therapyRecommendationRepository = therapyRecommendationRepository;
  }

  public DiagnoseDto addDiagnose(DiagnoseDto diagnoseDto) throws ForeignKeyException {
    if (!episodeService.isEpisodeExist(diagnoseDto.getEpisodeId())) {
      throw new ForeignKeyException("Episode with ID " + diagnoseDto.getEpisodeId() + " does not exist");
    }

    for (UUID histologyReportId : diagnoseDto.getHistologyResults()) {
      if (!histologyReportService.isHistologyReportExist(histologyReportId)) {
        throw new ForeignKeyException("HistologyReport with ID " + histologyReportId + " does not exist");
      }
    }

    Diagnose diagnose = diagnoseMapper.toEntity(diagnoseDto);
    Diagnose savedDiagnose = diagnoseRepository.save(diagnose);

    return diagnoseMapper.toDto(savedDiagnose);
  }

  public boolean isDiagnoseExist(UUID diagnoseId) {
    return diagnoseRepository.existsById(diagnoseId);
  }

  public DiagnoseDto getDiagnose(UUID diagnoseId) {

    Diagnose savedDiagnose = diagnoseRepository.getReferenceById(diagnoseId);

    return diagnoseMapper.toDto(savedDiagnose);
  }

  public Diagnose getDiagnoseReference(UUID diagnoseId) {

    return diagnoseRepository.getReferenceById(diagnoseId);
  }

  public List<DiagnoseDto> getAllDiagnoses(UUID episodeId) throws ForeignKeyException {

    if (!episodeService.isEpisodeExist(episodeId)) {
      throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
    }

    List<Diagnose> SavedDiagnoses = diagnoseRepository.getAllByEpisodeId(episodeId);

    return SavedDiagnoses.stream().map(diagnoseMapper::toDto).toList();
  }

  public DiagnoseDto updateDiagnose(UUID diagnoseId, DiagnoseDto diagnoseDto) throws ForeignKeyException {

    for (UUID histologyReportId : diagnoseDto.getHistologyResults()) {
      if (!histologyReportService.isHistologyReportExist(histologyReportId)) {
        throw new ForeignKeyException("HistologyReport with ID " + histologyReportId + " does not exist");
      }
    }

    if (!isGermlineDiagnosisCorrect(diagnoseDto)) {
      throw new IllegalArgumentException("The germlineDiagnosis attribute of Diagnose with ID " + diagnoseDto.getId() + " is not correct");
    }

    Diagnose diagnose = diagnoseMapper.toEntity(diagnoseDto);
    Diagnose updatedDiagnose = diagnoseRepository.save(diagnose);

    return diagnoseMapper.toDto(updatedDiagnose);
  }
  
  public void deleteDiagnose(UUID diagnoseId) throws IllegalStateException {

    if (
      carePlanRepository.existsByDiagnosisId(diagnoseId) ||
      guidelineTherapyRepository.existsByDiagnosisId(diagnoseId) ||
      studyInclusionRequestRepository.existsByDiagnosisId(diagnoseId) ||
      therapyRecommendationRepository.existsByDiagnosisId(diagnoseId)
      )
      {
        throw new IllegalStateException("The diagnosis cannot be deleted as it is referenced in other tables.");
      }
    
    diagnoseRepository.deleteById(diagnoseId);
  }

  /*
   * This method validates the completeness of a germline diagnosis in the given DiagnoseDto object.
   *
   * The following logic is applied to determine correctness:
   * 1. If `isGermlineDiagnosisExist` is true:
   *    - The `germlineDiagnosisIcd10` field must not be null.
   * 2. If `isGermlineDiagnosisExist` is false:
   *    - The `germlineDiagnosisIcd10` field must be null or its `code` property must be empty.
   *
   * The method returns true if the `DiagnoseDto` object satisfies these conditions,
   * indicating that the germline diagnosis information is considered correct and complete.
   * Otherwise, it returns false.
   */
  public boolean isGermlineDiagnosisCorrect(DiagnoseDto diagnoseDto) {
    if (diagnoseDto.getIsGermlineDiagnosisExist()) {
      // If germline diagnosis exists, ensure the ICD-10 data is present
      return diagnoseDto.getGermlineDiagnosisIcd10() != null && diagnoseDto.getGermlineDiagnosisIcd10().getCode() != null;
    } else {
      // If germline diagnosis does not exist, ensure the ICD-10 data is null or empty
      return diagnoseDto.getGermlineDiagnosisIcd10().getCode().isEmpty();
    }
  }


}
