package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StepRepository extends JpaRepository<Step, Integer> {

}
