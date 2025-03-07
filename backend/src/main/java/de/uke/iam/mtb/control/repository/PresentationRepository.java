package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.Presentation;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresentationRepository extends JpaRepository<Presentation, UUID> {

  List<Presentation> findAllByEpisodeId(UUID episodeId);

  boolean existsByEpisodeIdAndDateOfPresentation(UUID episodeId, LocalDate dateOfPresentation);

}
