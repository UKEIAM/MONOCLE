package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.Workflow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Integer> {

  // Custom query to retrieve workflows with steps having no parent step
  @Query("SELECT w FROM Workflow w " +
      "LEFT JOIN FETCH w.steps s1 " +
      "WHERE s1.parentStep IS NULL " +
      "ORDER BY w.id, s1.id")
  List<Workflow> findAllWorkflowsWithFilteredAndSortedSteps();
}
