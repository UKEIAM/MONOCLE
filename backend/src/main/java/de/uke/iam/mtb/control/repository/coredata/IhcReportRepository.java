package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.IhcReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IhcReportRepository extends JpaRepository<IhcReport, UUID> {
    public List<IhcReport> getAllByEpisodeId(UUID episodeId);

    @Query("SELECT EXISTS (SELECT 1 FROM IhcReport r WHERE r.specimen.id = :specimenId)")
    boolean existsBySpecimenId(@Param("specimenId") UUID specimenId);
}
