package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.MolecularTherapyResponseDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.coredata.MolecularTherapyResponse;
import de.uke.iam.mtb.control.models.mapper.coredata.MolecularTherapyResponseMapper;
import de.uke.iam.mtb.control.repository.coredata.MolecularTherapyResponseRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MolecularTherapyResponseService {

    private MolecularTherapyResponseRepository molecularTherapyResponseRepository;
    private MolecularTherapyResponseMapper molecularTherapyResponseMapper;
    private EpisodeService episodeService;
    private MolecularTherapyService molecularTherapyService;

    public MolecularTherapyResponseService(MolecularTherapyResponseRepository molecularTherapyResponseRepository, MolecularTherapyResponseMapper molecularTherapyResponseMapper,
                                           EpisodeService episodeService,
                                           MolecularTherapyService molecularTherapyService) {

        this.molecularTherapyResponseRepository = molecularTherapyResponseRepository;
        this.molecularTherapyResponseMapper = molecularTherapyResponseMapper;
        this.episodeService = episodeService;
        this.molecularTherapyService = molecularTherapyService;

    }

    public MolecularTherapyResponseDto addMolecularTherapyResponse(MolecularTherapyResponseDto molecularTherapyResponseDto) throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(molecularTherapyResponseDto.getEpisodeId())) {
            throw new ForeignKeyException("Episode with ID " + molecularTherapyResponseDto.getEpisodeId() + " does not exist");
        }

        if (!molecularTherapyService.isMolecularTherapyExist(molecularTherapyResponseDto.getTherapy())) {
            throw new ForeignKeyException("MolecularTherapy with ID " + molecularTherapyResponseDto.getEpisodeId() + " does not exist");
        }

        MolecularTherapyResponse molecularTherapyResponse = molecularTherapyResponseMapper.toEntity(molecularTherapyResponseDto);
        MolecularTherapyResponse savedMolecularTherapyResponse = molecularTherapyResponseRepository.save(molecularTherapyResponse);

        return molecularTherapyResponseMapper.toDto(savedMolecularTherapyResponse);
    }

    public boolean isMolecularTherapyResponseExist(UUID molecularTherapyResponseId) {
        return molecularTherapyResponseRepository.existsById(molecularTherapyResponseId);
    }

    public MolecularTherapyResponseDto getMolecularTherapyResponse(UUID molecularTherapyResponseId) {

        MolecularTherapyResponse savedMolecularTherapyResponse = molecularTherapyResponseRepository.getById(molecularTherapyResponseId);

        return molecularTherapyResponseMapper.toDto(savedMolecularTherapyResponse);

    }

    public MolecularTherapyResponse getMolecularTherapyResponseReference(UUID molecularTherapyResponseId) {

        return molecularTherapyResponseRepository.getReferenceById(molecularTherapyResponseId);
    }

    public List<MolecularTherapyResponseDto> getAllMolecularTherapyResponses(UUID episodeId) throws ForeignKeyException {

        if (!episodeService.isEpisodeExist(episodeId)) {
            throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
        }

        List<MolecularTherapyResponse> savedMolecularTherapyResponse = molecularTherapyResponseRepository.getAllByEpisodeId(episodeId);

        return savedMolecularTherapyResponse.stream().map(molecularTherapyResponseMapper::toDto).toList();
    }

    public MolecularTherapyResponseDto updateMolecularTherapyResponse(UUID molecularTherapyResponseId, MolecularTherapyResponseDto molecularTherapyResponseDto) throws ForeignKeyException {

        if (!molecularTherapyService.isMolecularTherapyExist(molecularTherapyResponseDto.getTherapy())) {
            throw new ForeignKeyException("MolecularTherapy with ID " + molecularTherapyResponseDto.getEpisodeId() + " does not exist");
        }

        MolecularTherapyResponse molecularTherapyResponse = molecularTherapyResponseMapper.toEntity(molecularTherapyResponseDto);
        MolecularTherapyResponse updatedMolecularTherapyResponse = molecularTherapyResponseRepository.save(molecularTherapyResponse);

        return molecularTherapyResponseMapper.toDto(updatedMolecularTherapyResponse);
    }

    public void deleteMolecularTherapyResponse(UUID molecularTherapyResponseId) {

        molecularTherapyResponseRepository.deleteById(molecularTherapyResponseId);

    }


}
