package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.json.CodeListConverter;
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
@Table(name = "molecular_pathology_finding")
// Hibernate will execute the SQL statement specified in the @SQLDelete annotation, which sets the deletedAt field to the current timestamp.
@SQLDelete(sql = "UPDATE molecular_pathology_finding SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
// The @Where annotation ensures that Hibernate only retrieves records that have not been marked as deleted.
@Where(clause = "deleted_at IS NULL")
//Please note that this approach requires you to handle transactions manually to ensure that the delete() operation is actually executed.
// You can do this by calling the flush() method on your repository or by using the @Transactional annotation on your service methods.
public class MolecularPathologyFinding {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  @ManyToOne
  private Episode episode;
  // a reference to the specimen
  @ManyToOne
  private Specimen specimen;
  private UUID performingInstitute;
  private LocalDate issuedOn;
  @Column(columnDefinition = "TEXT")
  private String note;
  @Convert(converter = CodeListConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private List<Code> typeOfDiagnostic;

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
