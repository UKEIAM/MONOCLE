package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.KcReport;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KcReportRepository extends JpaRepository<KcReport, UUID> {

    boolean existsByFileName(String fileName);

    KcReport findByFileName(String fileName);

    List<KcReport> findAllByEpisodeId(UUID episodeId);

}
