package de.uke.iam.mtb.control.repository.coredata;

import de.uke.iam.mtb.control.models.coredata.Specimen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpecimenRepository extends JpaRepository<Specimen, UUID> {

    @Query("SELECT r FROM Specimen r WHERE r.episode.id = :episodeId ORDER BY r.createdAt ASC")
    public List<Specimen> getAllByEpisodeId(@Param("episodeId") UUID episodeId);

    @Query("SELECT r FROM Specimen r WHERE r.labelling = :labelling ORDER BY r.createdAt ASC")
    public List<Specimen> getAllByLabelling(@Param("labelling") String labelling);

    @Query("SELECT EXISTS (SELECT r FROM Specimen r WHERE r.labelling = :labelling)")
    public boolean isSpecimenWithLabelExists(@Param("labelling") String labelling);

}
