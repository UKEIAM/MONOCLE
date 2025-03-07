package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.ClaimResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClaimResponseRepository extends JpaRepository<ClaimResponse, UUID> {

  @Query("SELECT r FROM ClaimResponse r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
  public List<ClaimResponse> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

  @Query("SELECT EXISTS (SELECT 1 FROM ClaimResponse r WHERE r.claim.id = :claimId)")
  public Boolean existsByClaimId(@Param("claimId") UUID claimId);

}
