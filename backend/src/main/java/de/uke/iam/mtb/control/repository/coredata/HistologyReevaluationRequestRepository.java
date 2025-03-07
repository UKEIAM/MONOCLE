package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.HistologyReevaluationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface HistologyReevaluationRequestRepository extends JpaRepository<HistologyReevaluationRequest, UUID> {

    @Query("SELECT r FROM HistologyReevaluationRequest r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
    public List<HistologyReevaluationRequest> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

    @Query("SELECT EXISTS (SELECT 1 FROM HistologyReevaluationRequest r WHERE r.specimen.id = :specimenId)")
    public Boolean existsBySpecimenId(@Param("specimenId") UUID specimenId);

}
