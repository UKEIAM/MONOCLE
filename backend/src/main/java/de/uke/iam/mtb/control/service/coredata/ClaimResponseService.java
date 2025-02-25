package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.ClaimResponseDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.coredata.ClaimResponse;
import de.uke.iam.mtb.control.models.mapper.coredata.ClaimResponseMapper;
import de.uke.iam.mtb.control.repository.coredata.ClaimRepository;
import de.uke.iam.mtb.control.repository.coredata.ClaimResponseRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClaimResponseService {

  private ClaimResponseRepository claimResponseRepository;
  private ClaimResponseMapper claimResponseMapper;
  private EpisodeService episodeService;
  private ClaimService claimService;

  public ClaimResponseService(ClaimResponseRepository claimResponseRepository, ClaimRepository claimRepository, ClaimResponseMapper claimResponseMapper,
      EpisodeService episodeService,
      ClaimService claimService) {

    this.claimResponseRepository = claimResponseRepository;
    this.claimResponseMapper = claimResponseMapper;
    this.episodeService = episodeService;
    this.claimService = claimService;
  }

  public ClaimResponseDto addClaimResponse(ClaimResponseDto claimResponseDto) throws ForeignKeyException {
    if (!episodeService.isEpisodeExist(claimResponseDto.getEpisodeId())) {
      throw new ForeignKeyException("Episode with ID " + claimResponseDto.getEpisodeId() + " does not exist");
    }

    if (!claimService.isClaimExist(claimResponseDto.getClaim())) {
      throw new ForeignKeyException("Claim with ID " + claimResponseDto.getEpisodeId() + " does not exist");
    }

    ClaimResponse claimResponse = claimResponseMapper.toEntity(claimResponseDto);
    ClaimResponse savedClaimResponse = claimResponseRepository.save(claimResponse);

    return claimResponseMapper.toDto(savedClaimResponse);
  }

  public boolean isClaimResponseExist(UUID claimResponseId) {
    return claimResponseRepository.existsById(claimResponseId);
  }

  public ClaimResponseDto getClaimResponse(UUID claimResponseId) {

    ClaimResponse savedClaimResponse = claimResponseRepository.getById(claimResponseId);

    return claimResponseMapper.toDto(savedClaimResponse);

  }

  public ClaimResponse getClaimResponseReference(UUID claimResponseId) {

    return claimResponseRepository.getReferenceById(claimResponseId);
  }

  public List<ClaimResponseDto> getAllClaimResponses(UUID episodeId) throws ForeignKeyException {

    if (!episodeService.isEpisodeExist(episodeId)) {
      throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
    }

    List<ClaimResponse> savedClaimResponse = claimResponseRepository.getAllByEpisodeId(episodeId);

    return savedClaimResponse.stream().map(claimResponseMapper::toDto).toList();
  }

  public ClaimResponseDto updateClaimResponse(UUID claimResponseId, ClaimResponseDto claimResponseDto) throws ForeignKeyException {

    if (!claimService.isClaimExist(claimResponseDto.getClaim())) {
      throw new ForeignKeyException("Claim with ID " + claimResponseDto.getEpisodeId() + " does not exist");
    }

    ClaimResponse claimResponse = claimResponseMapper.toEntity(claimResponseDto);
    ClaimResponse updatedClaimResponse = claimResponseRepository.save(claimResponse);

    return claimResponseMapper.toDto(updatedClaimResponse);
  }

  public void deleteClaimResponse(UUID claimResponseId) {

    claimResponseRepository.deleteById(claimResponseId);

  }


}
