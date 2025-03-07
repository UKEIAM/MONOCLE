package de.uke.iam.mtb.control.models;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment")
@SQLDelete(sql = "UPDATE comment SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Comment {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  private String author;
  @Column(columnDefinition = "TEXT")
  private String comment;
  private Boolean highlighted;
  @ManyToOne
  @JoinColumn(name = "patient_id", referencedColumnName = "id")
  private Patient patient;
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