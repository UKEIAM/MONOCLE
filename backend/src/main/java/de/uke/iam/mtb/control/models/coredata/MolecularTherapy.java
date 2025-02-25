package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.json.CodeListConverter;
import de.uke.iam.mtb.control.models.converter.json.ValueSetConverter;
import de.uke.iam.mtb.control.models.enums.coredata.MolecularTherapyStatus;
import de.uke.iam.mtb.control.models.enums.coredata.RealisationOfTherapy;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "molecular_therapy")
@SQLDelete(sql = "UPDATE molecular_therapy SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class MolecularTherapy {
  // also known as Systemic therapy
  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  @ManyToOne
  @JoinColumn(name = "episode_id", referencedColumnName = "id")
  private Episode episode;
  private LocalDate recordedOn;
  @ManyToOne
  private TherapyRecommendation therapyRecommendation;
  @Column(columnDefinition = "TEXT")
  private String note;
  private MolecularTherapyStatus status;
  @Convert(converter = ValueSetConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private ValueSet notDoneReason;
  private LocalDate periodStart;
  private LocalDate periodEnd;
  @Convert(converter = CodeListConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private List<Code> medication;
  private String dosage;
  @Convert(converter = ValueSetConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private ValueSet reasonStopped;
  @Enumerated(EnumType.STRING)
  private RealisationOfTherapy realisation;
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
