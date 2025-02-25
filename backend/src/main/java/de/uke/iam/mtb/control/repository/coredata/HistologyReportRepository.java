package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.HistologyReport;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistologyReportRepository extends JpaRepository<HistologyReport, UUID> {

  @Query("SELECT r FROM HistologyReport r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
  public List<HistologyReport> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

  @Query("SELECT EXISTS (SELECT 1 FROM HistologyReport r WHERE r.specimen.id = :specimenId)")
  public Boolean existsBySpecimenId(@Param("specimenId") UUID specimenId);
}
