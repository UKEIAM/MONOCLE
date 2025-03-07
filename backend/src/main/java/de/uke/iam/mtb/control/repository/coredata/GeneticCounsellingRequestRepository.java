package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.GeneticCounsellingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GeneticCounsellingRequestRepository extends JpaRepository<GeneticCounsellingRequest, UUID> {

    @Query("SELECT r FROM GeneticCounsellingRequest r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
    public List<GeneticCounsellingRequest> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

}
