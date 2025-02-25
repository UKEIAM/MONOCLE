package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.CodeListConverter;
import de.uke.iam.mtb.control.models.converter.StringListConverter;
import de.uke.iam.mtb.control.models.converter.json.EvidenceLevelConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "therapy_recommendation")
// Hibernate will execute the SQL statement specified in the @SQLDelete annotation, which sets the deletedAt field to the current timestamp.
@SQLDelete(sql = "UPDATE therapy_recommendation SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
// The @Where annotation ensures that Hibernate only retrieves records that have not been marked as deleted.
@Where(clause = "deleted_at IS NULL")

public class TherapyRecommendation {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  @ManyToOne
  @JoinColumn(name = "episode_id", referencedColumnName = "id")
  private Episode episode;
  @ManyToOne
  private Diagnose diagnosis;
  @ManyToOne
  private NgsReport ngsReport;
  private LocalDate issuedOn;
  @Convert(converter = CodeListConverter.class)
  private List<Code> medication;
  private String priority;
  @Convert(converter = EvidenceLevelConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private EvidenceLevel levelOfEvidence;
  @Convert(converter = StringListConverter.class)
  private List<String> supportingVariants;
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
