package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.HistologyReevaluationRequestDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.coredata.HistologyReevaluationRequest;
import de.uke.iam.mtb.control.models.coredata.Specimen;
import de.uke.iam.mtb.control.models.mapper.coredata.HistologyReevaluationRequestMapper;
import de.uke.iam.mtb.control.repository.coredata.HistologyReevaluationRequestRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class HistologyReevaluationRequestService {

    private HistologyReevaluationRequestRepository histologyReevaluationRequestRepository;
    private HistologyReevaluationRequestMapper histologyReevaluationRequestMapper;
    private EpisodeService episodeService;
    private SpecimenService specimenService;

    public HistologyReevaluationRequestService(HistologyReevaluationRequestRepository histologyReevaluationRequestRepository, HistologyReevaluationRequestMapper histologyReevaluationRequestMapper,
                                               EpisodeService episodeService, SpecimenService specimenService) {

        this.histologyReevaluationRequestRepository = histologyReevaluationRequestRepository;
        this.histologyReevaluationRequestMapper = histologyReevaluationRequestMapper;
        this.episodeService = episodeService;
        this.specimenService = specimenService;

    }

    public HistologyReevaluationRequestDto addHistologyReevaluationRequest(HistologyReevaluationRequestDto histologyReevaluationRequestDto) throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(histologyReevaluationRequestDto.getEpisodeId())) {
            throw new ForeignKeyException("Episode with ID " + histologyReevaluationRequestDto.getEpisodeId() + " does not exist");
        }

        if (!specimenService.isSpecimenExist(histologyReevaluationRequestDto.getSpecimen())) {
            throw new ForeignKeyException("Specimen with ID " + histologyReevaluationRequestDto.getSpecimen() + " does not exist");
        }

        HistologyReevaluationRequest histologyReevaluationRequest = histologyReevaluationRequestMapper.toEntity(histologyReevaluationRequestDto);
        Episode episodeReference = episodeService.getEpisodeReference(histologyReevaluationRequestDto.getEpisodeId());
        Specimen specimenReference = specimenService.getSpecimenReference(histologyReevaluationRequestDto.getSpecimen());
        histologyReevaluationRequest.setEpisode(episodeReference);
        histologyReevaluationRequest.setSpecimen(specimenReference);
        HistologyReevaluationRequest savedHistologyReevaluationRequest = histologyReevaluationRequestRepository.save(histologyReevaluationRequest);

        return histologyReevaluationRequestMapper.toDto(savedHistologyReevaluationRequest);
    }

    public boolean isHistologyReevaluationRequestExist(UUID histologyReevaluationRequestId) {
        return histologyReevaluationRequestRepository.existsById(histologyReevaluationRequestId);
    }

    public HistologyReevaluationRequestDto getHistologyReevaluationRequest(UUID histologyReevaluationRequestId) {
        HistologyReevaluationRequest savedHistologyReevaluationRequest = histologyReevaluationRequestRepository.findById(histologyReevaluationRequestId).orElse(null);
        return histologyReevaluationRequestMapper.toDto(savedHistologyReevaluationRequest);
    }

    public HistologyReevaluationRequest getHistologyReevaluationRequestReference(UUID histologyReevaluationRequestId) {

        return histologyReevaluationRequestRepository.getReferenceById(histologyReevaluationRequestId);
    }

    public List<HistologyReevaluationRequestDto> getAllHistologyReevaluationRequests(UUID episodeId) throws ForeignKeyException {

        if (!episodeService.isEpisodeExist(episodeId)) {
            throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
        }

        List<HistologyReevaluationRequest> savedHistologyReevaluationRequest = histologyReevaluationRequestRepository.getAllByEpisodeId(episodeId);

        return savedHistologyReevaluationRequest.stream().map(histologyReevaluationRequestMapper::toDto).toList();
    }

    public HistologyReevaluationRequestDto updateHistologyReevaluationRequest(UUID histologyReevaluationRequestId, HistologyReevaluationRequestDto histologyReevaluationRequestDto)
            throws ForeignKeyException {

        if (!specimenService.isSpecimenExist(histologyReevaluationRequestDto.getSpecimen())) {
            throw new ForeignKeyException("Specimen with ID " + histologyReevaluationRequestDto.getSpecimen() + " does not exist");
        }

        HistologyReevaluationRequest histologyReevaluationRequest = histologyReevaluationRequestMapper.toEntity(histologyReevaluationRequestDto);
        HistologyReevaluationRequest updatedHistologyReevaluationRequest = histologyReevaluationRequestRepository.save(histologyReevaluationRequest);

        return histologyReevaluationRequestMapper.toDto(updatedHistologyReevaluationRequest);
    }

    public void deleteHistologyReevaluationRequest(UUID histologyReevaluationRequestId) {

        histologyReevaluationRequestRepository.deleteById(histologyReevaluationRequestId);

    }

}
