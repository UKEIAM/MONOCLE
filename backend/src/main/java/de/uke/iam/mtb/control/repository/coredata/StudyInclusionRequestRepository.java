package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.StudyInclusionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StudyInclusionRequestRepository extends JpaRepository<StudyInclusionRequest, UUID> {

    @Query("SELECT r FROM StudyInclusionRequest r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
    public List<StudyInclusionRequest> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

    @Query("SELECT EXISTS (SELECT 1 FROM StudyInclusionRequest r WHERE r.diagnose.id = :diagnosisId)")
    public Boolean existsByDiagnosisId(@Param("diagnosisId") UUID diagnosisId);

}
