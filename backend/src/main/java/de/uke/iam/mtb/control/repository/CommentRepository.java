package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.Comment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

  List<Comment> findAllByPatientId(UUID patientId);
}
