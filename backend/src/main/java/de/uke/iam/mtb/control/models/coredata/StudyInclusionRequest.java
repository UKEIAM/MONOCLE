package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.json.LevelOfEvidenceConverter;
import de.uke.iam.mtb.control.models.converter.json.StringListConverter;
import de.uke.iam.mtb.control.models.converter.json.StudyListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "study_inclusion_request")
// Hibernate will execute the SQL statement specified in the @SQLDelete annotation, which sets the deletedAt field to the current timestamp.
@SQLDelete(sql = "UPDATE study_inclusion_request SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
// The @Where annotation ensures that Hibernate only retrieves records that have not been marked as deleted.
@Where(clause = "deleted_at IS NULL")

public class StudyInclusionRequest {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "episode_id", referencedColumnName = "id")
    private Episode episode;
    private LocalDate issuedOn;
    // TODO: delete this comment if not needed
    // private String nctNumber;
    // private String eudraCTNumber;
    // private String drksNumber;
    // private String eudamedNumber;
    @ManyToOne
    private Diagnose diagnose;

    @Convert(converter = StudyListConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private List<Study> studies;
    @Convert(converter = LevelOfEvidenceConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private EvidenceLevel levelOfEvidence;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "study_inclusion_request_ngs_report", // specify the name of the join table here
        joinColumns = @JoinColumn(name = "study_inclusion_request_id"), // specify the name of the column in the join table that references the current entity
        inverseJoinColumns = @JoinColumn(name = "ngs_report_id") // specify the name of the column in the join table that references
        // the other entity
    )
    private List<NgsReport> ngsReports;
    @Convert(converter = StringListConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private List<String> supportingVariants;
    // private Boolean studyRecommendation;
    // private List<Study> study;

    @Column(updatable = false)
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now(); // The createdAt field is set to the current timestamp when the entity is persisted
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now(); // The updatedAt field is set to the current timestamp when the entity is updated
    }


}
