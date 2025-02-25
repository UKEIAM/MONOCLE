package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.GeneticCounsellingRequestDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.coredata.GeneticCounsellingRequest;
import de.uke.iam.mtb.control.models.mapper.coredata.GeneticCounsellingRequestMapper;
import de.uke.iam.mtb.control.repository.coredata.CarePlanRepository;
import de.uke.iam.mtb.control.repository.coredata.GeneticCounsellingRequestRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GeneticCounsellingRequestService {

    private final GeneticCounsellingRequestRepository geneticCounsellingRequestRepository;
    private final GeneticCounsellingRequestMapper geneticCounsellingRequestMapper;
    private final EpisodeService episodeService;

    private final CarePlanRepository carePlanRepository;

    public GeneticCounsellingRequestService(GeneticCounsellingRequestRepository geneticCounsellingRequestRepository,
        CarePlanRepository carePlanRepository, GeneticCounsellingRequestMapper geneticCounsellingRequestMapper,
        EpisodeService episodeService) {

        this.geneticCounsellingRequestRepository = geneticCounsellingRequestRepository;
        this.geneticCounsellingRequestMapper = geneticCounsellingRequestMapper;
        this.episodeService = episodeService;

        this.carePlanRepository = carePlanRepository;
    }

    public GeneticCounsellingRequestDto addGeneticCounsellingRequest(GeneticCounsellingRequestDto geneticCounsellingRequestDto)
        throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(geneticCounsellingRequestDto.getEpisodeId())) {
            throw new ForeignKeyException("Episode with ID " + geneticCounsellingRequestDto.getEpisodeId() + " does not exist");
        }

        GeneticCounsellingRequest geneticCounsellingRequest = geneticCounsellingRequestMapper.toEntity(geneticCounsellingRequestDto);
        Episode episodeReference = episodeService.getEpisodeReference(geneticCounsellingRequestDto.getEpisodeId());
        geneticCounsellingRequest.setEpisode(episodeReference);
        GeneticCounsellingRequest savedGeneticCounsellingRequest = geneticCounsellingRequestRepository.save(geneticCounsellingRequest);

        return geneticCounsellingRequestMapper.toDto(savedGeneticCounsellingRequest);
    }

    public boolean isGeneticCounsellingRequestExist(UUID geneticCounsellingRequestId) {
        return geneticCounsellingRequestRepository.existsById(geneticCounsellingRequestId);
    }

    public GeneticCounsellingRequestDto getGeneticCounsellingRequest(UUID geneticCounsellingRequestId) {
        GeneticCounsellingRequest savedGeneticCounsellingRequest = geneticCounsellingRequestRepository.findById(geneticCounsellingRequestId).orElse(null);
        return geneticCounsellingRequestMapper.toDto(savedGeneticCounsellingRequest);
    }

    public GeneticCounsellingRequest getGeneticCounsellingRequestReference(UUID geneticCounsellingRequestId) {

        return geneticCounsellingRequestRepository.getReferenceById(geneticCounsellingRequestId);
    }

    public List<GeneticCounsellingRequestDto> getAllGeneticCounsellingRequests(UUID episodeId) throws ForeignKeyException {

        if (!episodeService.isEpisodeExist(episodeId)) {
            throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
        }

        List<GeneticCounsellingRequest> savedGeneticCounsellingRequest = geneticCounsellingRequestRepository.getAllByEpisodeId(episodeId);

        return savedGeneticCounsellingRequest.stream().map(geneticCounsellingRequestMapper::toDto).toList();
    }

    public GeneticCounsellingRequestDto updateGeneticCounsellingRequest(UUID geneticCounsellingRequestId, GeneticCounsellingRequestDto geneticCounsellingRequestDto)
            throws ForeignKeyException {

        GeneticCounsellingRequest geneticCounsellingRequest = geneticCounsellingRequestMapper.toEntity(geneticCounsellingRequestDto);
        GeneticCounsellingRequest updatedGeneticCounsellingRequest = geneticCounsellingRequestRepository.save(geneticCounsellingRequest);
        return geneticCounsellingRequestMapper.toDto(updatedGeneticCounsellingRequest);
    }

    public void deleteGeneticCounsellingRequest(UUID geneticCounsellingRequestId) {

        if (carePlanRepository.existsByGeneticCounsellingRequestId(geneticCounsellingRequestId)) {
            throw new IllegalStateException("The Genetic Counselling Request cannot be deleted as it is referenced in other tables.");
        }

        geneticCounsellingRequestRepository.deleteById(geneticCounsellingRequestId);

    }

}
