package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.RebiopsyRequestDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.coredata.RebiopsyRequest;
import de.uke.iam.mtb.control.models.coredata.Specimen;
import de.uke.iam.mtb.control.models.mapper.coredata.RebiopsyRequestMapper;
import de.uke.iam.mtb.control.repository.coredata.RebiopsyRequestRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RebiopsyRequestService {

  @PersistenceContext
  private EntityManager entityManager;

    private RebiopsyRequestRepository rebiopsyRequestRepository;
    private RebiopsyRequestMapper rebiopsyRequestMapper;
    private EpisodeService episodeService;
    private SpecimenService specimenService;

    public RebiopsyRequestService(RebiopsyRequestRepository rebiopsyRequestRepository, RebiopsyRequestMapper rebiopsyRequestMapper,
                                  EpisodeService episodeService, SpecimenService specimenService) {

        this.rebiopsyRequestRepository = rebiopsyRequestRepository;
        this.rebiopsyRequestMapper = rebiopsyRequestMapper;
        this.episodeService = episodeService;
        this.specimenService = specimenService;

    }

    public RebiopsyRequestDto addRebiopsyRequest(RebiopsyRequestDto rebiopsyRequestDto) throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(rebiopsyRequestDto.getEpisodeId())) {
            throw new ForeignKeyException("Episode with ID " + rebiopsyRequestDto.getEpisodeId() + " does not exist");
        }

        if (!specimenService.isSpecimenExist(rebiopsyRequestDto.getSpecimen())) {
            throw new ForeignKeyException("Specimen with ID " + rebiopsyRequestDto.getSpecimen() + " does not exist");
        }

        RebiopsyRequest rebiopsyRequest = rebiopsyRequestMapper.toEntity(rebiopsyRequestDto);
        Episode episodeReference = episodeService.getEpisodeReference(rebiopsyRequestDto.getEpisodeId());
        Specimen specimenReference = specimenService.getSpecimenReference(rebiopsyRequestDto.getSpecimen());
        rebiopsyRequest.setEpisode(episodeReference);
        rebiopsyRequest.setSpecimen(specimenReference);
        RebiopsyRequest savedRebiopsyRequest = rebiopsyRequestRepository.save(rebiopsyRequest);

        return rebiopsyRequestMapper.toDto(savedRebiopsyRequest);
    }

    public boolean isRebiopsyRequestExist(UUID rebiopsyRequestId) {
        return rebiopsyRequestRepository.existsById(rebiopsyRequestId);
    }

    public RebiopsyRequestDto getRebiopsyRequest(UUID rebiopsyRequestId) {
        RebiopsyRequest savedRebiopsyRequest = rebiopsyRequestRepository.findById(rebiopsyRequestId).orElse(null);
        return rebiopsyRequestMapper.toDto(savedRebiopsyRequest);
    }

    public RebiopsyRequest getRebiopsyRequestReference(UUID rebiopsyRequestId) {

        return rebiopsyRequestRepository.getReferenceById(rebiopsyRequestId);
    }

    public List<RebiopsyRequestDto> getAllRebiopsyRequests(UUID episodeId) throws ForeignKeyException {

        if (!episodeService.isEpisodeExist(episodeId)) {
            throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
        }

        List<RebiopsyRequest> savedRebiopsyRequest = rebiopsyRequestRepository.getAllByEpisodeId(episodeId);

        return savedRebiopsyRequest.stream().map(rebiopsyRequestMapper::toDto).toList();
    }

    public RebiopsyRequestDto updateRebiopsyRequest(UUID rebiopsyRequestId, RebiopsyRequestDto rebiopsyRequestDto)
            throws ForeignKeyException {

        if (!specimenService.isSpecimenExist(rebiopsyRequestDto.getSpecimen())) {
            throw new ForeignKeyException("Specimen with ID " + rebiopsyRequestDto.getSpecimen() + " does not exist");
        }

        RebiopsyRequest rebiopsyRequest = rebiopsyRequestMapper.toEntity(rebiopsyRequestDto);
        RebiopsyRequest updatedRebiopsyRequest = rebiopsyRequestRepository.save(rebiopsyRequest);

        return rebiopsyRequestMapper.toDto(updatedRebiopsyRequest);
    }
  
    public Boolean isRebiopsyRequestReferenced(UUID rebiopsyRequestId) {
        // Fix: Since it is not possible to access an intermediate table using Hibernate without creating a model,
        // a conventional SQL statement is executed here. This can also be replaced by jOOQ after the refactoring.
        return (Boolean) entityManager.createNativeQuery("SELECT EXISTS (SELECT 1 FROM care_plan_rebiopsy_request WHERE rebiopsy_request_id = '"+rebiopsyRequestId+"') AS exists;").getSingleResult();
    }

    public void deleteRebiopsyRequest(UUID rebiopsyRequestId) {
        
        if (isRebiopsyRequestReferenced(rebiopsyRequestId)) {
            throw new IllegalStateException("The Rebiopsy Request cannot be deleted as it is referenced in other tables.");
        }

        rebiopsyRequestRepository.deleteById(rebiopsyRequestId);

    }

}
