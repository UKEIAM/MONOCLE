package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.FamilyMemberDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FamilyMemberDiagnosisRepository extends JpaRepository<FamilyMemberDiagnosis, UUID> {

    @Query("SELECT r FROM FamilyMemberDiagnosis r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
    public List<FamilyMemberDiagnosis> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

}