package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.TransferJob;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import de.uke.iam.mtb.control.models.enums.JobStatus;

@Repository
public interface BwhcTransferRepository extends JpaRepository<TransferJob, UUID> {
    List<TransferJob> getAllByStatus(@Param("status") JobStatus status);

}
