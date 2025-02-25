package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.MolecularTherapyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MolecularTherapyResponseRepository extends JpaRepository<MolecularTherapyResponse, UUID> {

    @Query("SELECT r FROM MolecularTherapyResponse r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
    public List<MolecularTherapyResponse> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

    @Query("SELECT EXISTS (SELECT 1 FROM MolecularTherapyResponse r WHERE r.therapy.id = :therapyId)")
    public Boolean existsByTherapyId(@Param("therapyId") UUID therapyId);
}
