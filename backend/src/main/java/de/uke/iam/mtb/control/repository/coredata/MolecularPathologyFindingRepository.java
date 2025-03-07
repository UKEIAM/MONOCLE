package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.MolecularPathologyFinding;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MolecularPathologyFindingRepository extends JpaRepository<MolecularPathologyFinding, UUID> {

  @Query("SELECT r FROM MolecularPathologyFinding r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
  public List<MolecularPathologyFinding> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

  @Query("SELECT EXISTS (SELECT 1 FROM MolecularPathologyFinding r WHERE r.specimen.id = :specimenId)")
  public Boolean existsBySpecimenId(@Param("specimenId") UUID specimenId);
}
