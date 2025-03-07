package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.LabNumber;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LabNumberRepository extends JpaRepository<LabNumber, String> {

  Optional<LabNumber> findById(String labNumberId);
  List<LabNumber> findAllByAssigned(Boolean isAssigned);

  @Query("SELECT EXISTS (SELECT r FROM LabNumber r WHERE r.id = :id)")
  public Boolean isLabNumberExists(@Param("id") String id);

  @Query("SELECT EXISTS (SELECT r FROM LabNumber r WHERE r.id = :id AND r.assigned = true)")
  public Boolean isLabNumberAlreadyAssigned(@Param("id") String id);
}
