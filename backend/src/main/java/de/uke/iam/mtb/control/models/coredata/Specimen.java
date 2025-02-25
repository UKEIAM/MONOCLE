package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.json.CodeConverter;
import de.uke.iam.mtb.control.models.converter.json.SpecimenCollectionConverter;
import de.uke.iam.mtb.control.models.enums.coredata.SpecimenType;
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
@Table(name = "specimen")
// Hibernate will execute the SQL statement specified in the @SQLDelete annotation, which sets the deletedAt field to the current timestamp.
@SQLDelete(sql = "UPDATE specimen SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
// The @Where annotation ensures that Hibernate only retrieves records that have not been marked as deleted.
@Where(clause = "deleted_at IS NULL")
//Please note that this approach requires you to handle transactions manually to ensure that the delete() operation is actually executed.
// You can do this by calling the flush() method on your repository or by using the @Transactional annotation on your service methods.
public class Specimen {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  private String labelling;
  @ManyToOne
  @JoinColumn(name = "episode_id", referencedColumnName = "id")
  private Episode episode;
  @Convert(converter = CodeConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private Code icd10;
  private SpecimenType type;
  @Convert(converter = SpecimenCollectionConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private SpecimenCollection collection;
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
