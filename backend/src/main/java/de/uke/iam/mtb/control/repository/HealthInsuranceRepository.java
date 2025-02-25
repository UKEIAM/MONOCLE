package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.HealthInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthInsuranceRepository extends JpaRepository<HealthInsurance, Long> {

}
