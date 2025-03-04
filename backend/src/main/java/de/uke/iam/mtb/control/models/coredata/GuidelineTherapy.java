package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.json.CodeConverter;
import de.uke.iam.mtb.control.models.converter.json.CodeListConverter;
import de.uke.iam.mtb.control.models.converter.json.ValueSetConverter;
import de.uke.iam.mtb.control.models.enums.coredata.MolecularTherapyIntention;
import de.uke.iam.mtb.control.models.enums.coredata.MolecularTherapyProcedurePosition;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
@Table(name = "guideline_therapy")
@SQLDelete(sql = "UPDATE guideline_therapy SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class GuidelineTherapy {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  @ManyToOne
  private Episode episode;
  // a reference to the specimen
  @ManyToOne
  private Diagnose diagnosis;
  private Integer therapyLine;
  @Convert(converter = CodeListConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private List<Code> medication;

  private LocalDate periodStart;
  private LocalDate periodEnd;
  @Convert(converter = ValueSetConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private ValueSet reasonStopped;
  @Convert(converter = CodeConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private Code procedure;
  @Enumerated(EnumType.STRING)
  private MolecularTherapyProcedurePosition procedurePosition;
  @Enumerated(EnumType.STRING)
  private MolecularTherapyIntention intention;
  private LocalDate progressDate;
  @ManyToOne
  private MolecularTherapyResponse molecularTherapyResponse;

  @Column(updatable = false)
  private Instant createdAt;
  private Instant updatedAt;
  private Instant deletedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }

}
