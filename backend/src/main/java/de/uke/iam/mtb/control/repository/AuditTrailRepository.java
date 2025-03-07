package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.AuditTrailEntry;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrailEntry, UUID> {

}
