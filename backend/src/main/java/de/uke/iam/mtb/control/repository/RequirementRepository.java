package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.Requirement;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, UUID> {

  public Requirement findByEpisodeId(UUID episodeId);

  boolean existsByEpisodeId(UUID episodeId);

}
