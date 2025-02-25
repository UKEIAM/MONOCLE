package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.Issue;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, UUID> {

    Issue findByEpisodeId(UUID episodeId);
    boolean existsByEpisodeId(UUID episodeId);
}
