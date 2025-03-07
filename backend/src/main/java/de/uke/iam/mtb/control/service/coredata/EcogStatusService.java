package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.EcogStatusDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.coredata.EcogStatus;
import de.uke.iam.mtb.control.models.mapper.coredata.EcogStatusMapper;
import de.uke.iam.mtb.control.repository.coredata.EcogStatusRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EcogStatusService {

    private EcogStatusRepository ecogStatusRepository;
    private EcogStatusMapper ecogStatusMapper;
    private EpisodeService episodeService;

    public EcogStatusService(EcogStatusRepository ecogStatusRepository, EcogStatusMapper ecogStatusMapper, EpisodeService episodeService) {
        this.ecogStatusRepository = ecogStatusRepository;
        this.ecogStatusMapper = ecogStatusMapper;
        this.episodeService = episodeService;
    }

    public EcogStatusDto addEcogStatus(EcogStatusDto ecogStatusDto) throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(ecogStatusDto.getEpisodeId())) {
            throw new ForeignKeyException("Episode with ID " + ecogStatusDto.getEpisodeId() + " does not exist");
        }

        EcogStatus ecogStatus = ecogStatusMapper.toEntity(ecogStatusDto);
        EcogStatus savedEcogStatus = ecogStatusRepository.save(ecogStatus);

        return ecogStatusMapper.toDto(savedEcogStatus);
    }

    public boolean isEcogStatusExist(UUID ecogStatusId) {
        return ecogStatusRepository.existsById(ecogStatusId);
    }

    public EcogStatusDto getEcogStatus(UUID ecogStatusId) {

        EcogStatus savedEcogStatus = ecogStatusRepository.getById(ecogStatusId);

        return ecogStatusMapper.toDto(savedEcogStatus);

    }

    public EcogStatus getEcogStatusReference(UUID ecogStatusId) {

        return ecogStatusRepository.getReferenceById(ecogStatusId);
    }

    public List<EcogStatusDto> getAllEcogStatuss(UUID episodeId) throws ForeignKeyException {

        if (!episodeService.isEpisodeExist(episodeId)) {
            throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
        }

        List<EcogStatus> savedEcogStatus = ecogStatusRepository.getAllByEpisodeId(episodeId);

        return savedEcogStatus.stream().map(ecogStatusMapper::toDto).toList();
    }

    public EcogStatusDto updateEcogStatus(UUID ecogStatusId, EcogStatusDto ecogStatusDto) {

        EcogStatus ecogStatus = ecogStatusMapper.toEntity(ecogStatusDto);
        EcogStatus updatedEcogStatus = ecogStatusRepository.save(ecogStatus);

        return ecogStatusMapper.toDto(updatedEcogStatus);
    }

    public void deleteEcogStatus(UUID ecogStatusId) {

        ecogStatusRepository.deleteById(ecogStatusId);

    }


}
