package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.GuidelineTherapy;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuidelineTherapyRepository extends JpaRepository<GuidelineTherapy, UUID> {
  @Query("SELECT r FROM GuidelineTherapy r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
  public List<GuidelineTherapy> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

  @Query("SELECT EXISTS (SELECT 1 FROM GuidelineTherapy r WHERE r.diagnosis.id = :diagnosisId)")
  public Boolean existsByDiagnosisId(@Param("diagnosisId") UUID diagnosisId);

}
