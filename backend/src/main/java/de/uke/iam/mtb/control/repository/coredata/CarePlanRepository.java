package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.CarePlan;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarePlanRepository extends JpaRepository<CarePlan, UUID> {

  @Query("SELECT r FROM CarePlan r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
  public List<CarePlan> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

  @Query("SELECT EXISTS (SELECT 1 FROM CarePlan r WHERE r.diagnosis.id = :diagnosisId)")
  public Boolean existsByDiagnosisId(@Param("diagnosisId") UUID diagnosisId);

  @Query("SELECT EXISTS (SELECT 1 FROM CarePlan r WHERE r.geneticCounsellingRequest.id = :geneticCounsellingRequestId)")
  public Boolean existsByGeneticCounsellingRequestId(@Param("geneticCounsellingRequestId") UUID geneticCounsellingRequestId);
}
