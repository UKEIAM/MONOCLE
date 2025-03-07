package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.MolecularTherapy;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MolecularTherapyRepository extends JpaRepository<MolecularTherapy, UUID> {

  @Query("SELECT r FROM MolecularTherapy r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
  public List<MolecularTherapy> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

  @Query("SELECT EXISTS (SELECT 1 FROM MolecularTherapy r WHERE r.therapyRecommendation.id = :therapyRecommendationId)")
  public Boolean existsByTherapyRecommendationId(@Param("therapyRecommendationId") UUID therapyRecommendationId);
}
