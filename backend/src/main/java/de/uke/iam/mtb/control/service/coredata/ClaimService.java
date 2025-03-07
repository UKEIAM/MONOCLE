package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.ClaimDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.coredata.Claim;
import de.uke.iam.mtb.control.models.mapper.coredata.ClaimMapper;
import de.uke.iam.mtb.control.repository.coredata.ClaimRepository;
import de.uke.iam.mtb.control.repository.coredata.ClaimResponseRepository;
import de.uke.iam.mtb.control.repository.coredata.TherapyRecommendationRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClaimService {

  private ClaimRepository claimRepository;
  private ClaimMapper claimMapper;
  private EpisodeService episodeService;
  private TherapyRecommendationService therapyRecommendationService;

  private ClaimResponseRepository claimResponseRepository;

  public ClaimService(ClaimRepository claimRepository, ClaimResponseRepository claimResponseRepository, ClaimMapper claimMapper, EpisodeService episodeService,
      TherapyRecommendationService therapyRecommendationService) {

    this.claimRepository = claimRepository;
    this.claimMapper = claimMapper;
    this.episodeService = episodeService;
    this.therapyRecommendationService = therapyRecommendationService;

    this.claimResponseRepository = claimResponseRepository;

  }

  public ClaimDto addClaim(ClaimDto claimDto) throws ForeignKeyException {
    if (!episodeService.isEpisodeExist(claimDto.getEpisodeId())) {
      throw new ForeignKeyException("Episode with ID " + claimDto.getEpisodeId() + " does not exist");
    }

    if (!therapyRecommendationService.isTherapyRecommendationExist(claimDto.getTherapy())) {
      throw new ForeignKeyException("Therapy with ID " + claimDto.getEpisodeId() + " does not exist");
    }

    Claim claim = claimMapper.toEntity(claimDto);
    Claim savedClaim = claimRepository.save(claim);

    return claimMapper.toDto(savedClaim);
  }

  public boolean isClaimExist(UUID claimId) {
    return claimRepository.existsById(claimId);
  }

  public ClaimDto getClaim(UUID claimId) {

    Claim savedClaim = claimRepository.getById(claimId);

    return claimMapper.toDto(savedClaim);

  }

  public Claim getClaimReference(UUID claimId) {

    return claimRepository.getReferenceById(claimId);
  }

  public List<ClaimDto> getAllClaims(UUID episodeId) throws ForeignKeyException {

    if (!episodeService.isEpisodeExist(episodeId)) {
      throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
    }

    List<Claim> savedClaim = claimRepository.getAllByEpisodeId(episodeId);

    return savedClaim.stream().map(claimMapper::toDto).toList();
  }

  public ClaimDto updateClaim(UUID claimId, ClaimDto claimDto) throws ForeignKeyException {

    if (!therapyRecommendationService.isTherapyRecommendationExist(claimDto.getTherapy())) {
      throw new ForeignKeyException("Therapy with ID " + claimDto.getEpisodeId() + " does not exist");
    }

    Claim claim = claimMapper.toEntity(claimDto);
    Claim updatedClaim = claimRepository.save(claim);

    return claimMapper.toDto(updatedClaim);
  }

  public void deleteClaim(UUID claimId) {

    if (
      claimResponseRepository.existsByClaimId(claimId)
      )
      {
        throw new IllegalStateException("The claim cannot be deleted as it is referenced in other tables.");
      }

    claimRepository.deleteById(claimId);

  }


}
