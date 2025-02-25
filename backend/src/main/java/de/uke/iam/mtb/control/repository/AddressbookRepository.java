package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.AddressbookEntry;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressbookRepository extends JpaRepository<AddressbookEntry, UUID> {

}
