package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.NgsReport;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NgsReportRepository extends JpaRepository<NgsReport, String> {

    @Query("SELECT r FROM NgsReport r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
    public List<NgsReport> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

    @Query("SELECT EXISTS (SELECT r FROM NgsReport r WHERE r.specimen.id= :specimenId)")
    public boolean isSpecimenAlreadyInNGSReport(@Param("specimenId") UUID specimenId);

    @Query("SELECT EXISTS (SELECT 1 FROM NgsReport r WHERE r.specimen.id = :specimenId)")
    public Boolean existsBySpecimenId(@Param("specimenId") UUID specimenId);
}
