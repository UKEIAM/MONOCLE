package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.Diagnose;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiagnoseRepository extends JpaRepository<Diagnose, UUID> {

  @Query("SELECT r FROM Diagnose r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
  public List<Diagnose> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

}
