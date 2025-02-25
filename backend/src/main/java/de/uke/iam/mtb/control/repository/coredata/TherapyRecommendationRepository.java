package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.TherapyRecommendation;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TherapyRecommendationRepository extends JpaRepository<TherapyRecommendation, UUID> {

  @Query("SELECT r FROM TherapyRecommendation r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
  public List<TherapyRecommendation> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

  @Query("SELECT EXISTS (SELECT 1 FROM TherapyRecommendation r WHERE r.diagnosis.id = :diagnosisId)")
  public Boolean existsByDiagnosisId(@Param("diagnosisId") UUID diagnosisId);

  @Query("SELECT EXISTS (SELECT 1 FROM TherapyRecommendation r WHERE r.ngsReport.id = :ngsReportId)")
  public Boolean existsByNgsReportId(@Param("ngsReportId") String ngsReportId);
}
