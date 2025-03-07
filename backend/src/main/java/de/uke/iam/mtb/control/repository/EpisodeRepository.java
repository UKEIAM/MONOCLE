package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.Episode;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeRepository extends JpaRepository<Episode, UUID> {

    public boolean existsByPatientId(UUID patientId);

}
