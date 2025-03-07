package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.StudyInclusionRequestDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.coredata.Diagnose;
import de.uke.iam.mtb.control.models.coredata.StudyInclusionRequest;
import de.uke.iam.mtb.control.models.mapper.coredata.StudyInclusionRequestMapper;
import de.uke.iam.mtb.control.repository.coredata.StudyInclusionRequestRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StudyInclusionRequestService {

  @PersistenceContext
  private EntityManager entityManager;

    private StudyInclusionRequestRepository studyInclusionRequestRepository;
    private StudyInclusionRequestMapper studyInclusionRequestMapper;
    private EpisodeService episodeService;
    private DiagnoseService diagnoseService;

    public StudyInclusionRequestService(StudyInclusionRequestRepository studyInclusionRequestRepository, StudyInclusionRequestMapper studyInclusionRequestMapper,
                                        EpisodeService episodeService, DiagnoseService diagnoseService) {

        this.studyInclusionRequestRepository = studyInclusionRequestRepository;
        this.studyInclusionRequestMapper = studyInclusionRequestMapper;
        this.episodeService = episodeService;
        this.diagnoseService = diagnoseService;

    }

    public StudyInclusionRequestDto addStudyInclusionRequest(StudyInclusionRequestDto studyInclusionRequestDto) throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(studyInclusionRequestDto.getEpisodeId())) {
            throw new ForeignKeyException("Episode with ID " + studyInclusionRequestDto.getEpisodeId() + " does not exist");
        }
        if (!diagnoseService.isDiagnoseExist(studyInclusionRequestDto.getReason())) {
            throw new ForeignKeyException("Diagnose with ID " + studyInclusionRequestDto.getReason() + " does not exist");
        }

        StudyInclusionRequest studyInclusionRequest = studyInclusionRequestMapper.toEntity(studyInclusionRequestDto);
        Episode episodeReference = episodeService.getEpisodeReference(studyInclusionRequestDto.getEpisodeId());
        Diagnose diagnoseReference = diagnoseService.getDiagnoseReference(studyInclusionRequestDto.getReason());
        studyInclusionRequest.setEpisode(episodeReference);
        studyInclusionRequest.setDiagnose(diagnoseReference);
        StudyInclusionRequest savedStudyInclusionRequest = studyInclusionRequestRepository.save(studyInclusionRequest);

        return studyInclusionRequestMapper.toDto(savedStudyInclusionRequest);
    }

    public boolean isStudyInclusionRequestExist(UUID studyInclusionRequestId) {
        return studyInclusionRequestRepository.existsById(studyInclusionRequestId);
    }

    public StudyInclusionRequestDto getStudyInclusionRequest(UUID studyInclusionRequestId) {
        StudyInclusionRequest savedStudyInclusionRequest = studyInclusionRequestRepository.findById(studyInclusionRequestId).orElse(null);
        return studyInclusionRequestMapper.toDto(savedStudyInclusionRequest);
    }

    public StudyInclusionRequest getStudyInclusionRequestReference(UUID studyInclusionRequestId) {

        return studyInclusionRequestRepository.getReferenceById(studyInclusionRequestId);
    }

    public List<StudyInclusionRequestDto> getAllStudyInclusionRequests(UUID episodeId) throws ForeignKeyException {

        if (!episodeService.isEpisodeExist(episodeId)) {
            throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
        }

        List<StudyInclusionRequest> savedStudyInclusionRequest = studyInclusionRequestRepository.getAllByEpisodeId(episodeId);

        return savedStudyInclusionRequest.stream().map(studyInclusionRequestMapper::toDto).toList();
    }

    public StudyInclusionRequestDto updateStudyInclusionRequest(UUID studyInclusionRequestId, StudyInclusionRequestDto studyInclusionRequestDto)
            throws ForeignKeyException {

        if (!diagnoseService.isDiagnoseExist(studyInclusionRequestDto.getReason())) {
            throw new ForeignKeyException("Diagnose with ID " + studyInclusionRequestDto.getReason() + " does not exist");
        }

        StudyInclusionRequest studyInclusionRequest = studyInclusionRequestMapper.toEntity(studyInclusionRequestDto);
        StudyInclusionRequest updatedStudyInclusionRequest = studyInclusionRequestRepository.save(studyInclusionRequest);

        return studyInclusionRequestMapper.toDto(updatedStudyInclusionRequest);
    }
  
    public Boolean isStudyInclusionRequestReferenced(UUID studyInclusionRequestId) {
        // Fix: Since it is not possible to access an intermediate table using Hibernate without creating a model,
        // a conventional SQL statement is executed here. This can also be replaced by jOOQ after the refactoring.
        return (Boolean) entityManager.createNativeQuery("SELECT EXISTS (SELECT 1 FROM care_plan_study_inclusion_request WHERE study_inclusion_request = '"+studyInclusionRequestId+"') AS exists;").getSingleResult();
    }
    
    public void deleteStudyInclusionRequest(UUID studyInclusionRequestId) {
        
        if (isStudyInclusionRequestReferenced(studyInclusionRequestId)) {
            throw new IllegalStateException("The Study Inclusion Request cannot be deleted as it is referenced in other tables.");
        }
        
        studyInclusionRequestRepository.deleteById(studyInclusionRequestId);

    }

}
