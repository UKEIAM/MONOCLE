package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.RebiopsyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RebiopsyRequestRepository extends JpaRepository<RebiopsyRequest, UUID> {

    @Query("SELECT r FROM RebiopsyRequest r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
    public List<RebiopsyRequest> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

    @Query("SELECT EXISTS (SELECT 1 FROM RebiopsyRequest r WHERE r.specimen.id = :specimenId)")
    public Boolean existsBySpecimenId(@Param("specimenId") UUID specimenId);
}
