package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.ValueSetConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "family_member_diagnosis")
@SQLDelete(sql = "UPDATE family_member_diagnosis SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class FamilyMemberDiagnosis {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  @ManyToOne
  @JoinColumn(name = "episode_id", referencedColumnName = "id")
  private Episode episode;
  @Convert(converter = ValueSetConverter.class)
  private ValueSet relationship;
  @Column(columnDefinition = "TEXT")
  private String details;
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
