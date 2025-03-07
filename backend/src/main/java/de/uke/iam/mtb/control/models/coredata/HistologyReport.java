package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.json.TumorCellContentConverter;
import de.uke.iam.mtb.control.models.converter.json.TumorMorphologyConverter;
import de.uke.iam.mtb.control.models.enums.coredata.DifferentiationDegreeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
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
@Table(name = "histology_report")
@SQLDelete(sql = "UPDATE histology_report SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class HistologyReport {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  @ManyToOne
  private Episode episode;
  // a reference to the specimen
  @ManyToOne
  private Specimen specimen;
  private LocalDate issuedOn;
  @Convert(converter = TumorCellContentConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private TumorCellContent tumorCellContent;
  @Convert(converter = TumorMorphologyConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private TumorMorphology tumorMorphology;
  private DifferentiationDegreeType differentiationDegree;

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
