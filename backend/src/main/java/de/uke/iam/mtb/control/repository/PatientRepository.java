package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.Patient;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

  boolean existsBySoarianId(String soarianID);

  Optional<Patient> getBySoarianId(String soarianID);
}
