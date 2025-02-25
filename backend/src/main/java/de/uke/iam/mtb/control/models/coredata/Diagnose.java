package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.json.CodeConverter;
import de.uke.iam.mtb.control.models.converter.json.CodeListConverter;
import de.uke.iam.mtb.control.models.converter.json.StatusHistoryListConverter;
import de.uke.iam.mtb.control.models.enums.coredata.GuidelineTreatmentStatus;
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
@Table(name = "diagnose")
// Hibernate will execute the SQL statement specified in the @SQLDelete annotation, which sets the deletedAt field to the current timestamp.
@SQLDelete(sql = "UPDATE diagnose SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
// The @Where annotation ensures that Hibernate only retrieves records that have not been marked as deleted.
@Where(clause = "deleted_at IS NULL")
//Please note that this approach requires you to handle transactions manually to ensure that the delete() operation is actually executed.
// You can do this by calling the flush() method on your repository or by using the @Transactional annotation on your service methods.
public class Diagnose {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "episode_id", referencedColumnName = "id")
    private Episode episode;
    private LocalDate recordedOn;

    @Convert(converter = CodeConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private Code icd10;

    @Convert(converter = CodeConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private Code icdO3T;

    @Convert(converter = CodeConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private Code whoGrade;

    @Convert(converter = StatusHistoryListConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private List<StatusHistory> statusHistory;
    private GuidelineTreatmentStatus guidelineTreatmentStatus;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "diagnose_histology_report", // specify the name of the join table here
        joinColumns = @JoinColumn(name = "diagnose_id"),
        inverseJoinColumns = @JoinColumn(name = "histology_report_id")
    )
    private List<HistologyReport> histologyResults;

    private Boolean isGermlineDiagnosisExist;

    @Convert(converter = CodeConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private Code germlineDiagnosisIcd10;

    @Convert(converter = CodeListConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private List<Code> hpoIcd10;

    @Convert(converter = CodeConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private Code orphanetCode;

    @Convert(converter = CodeConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private Code alphaIdSeCode;

    @Convert(converter = CodeConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private Code tnmKey;

    @Convert(converter = CodeListConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private List<Code> altTumorKey;

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
