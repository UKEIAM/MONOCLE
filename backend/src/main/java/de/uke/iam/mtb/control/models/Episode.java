package de.uke.iam.mtb.control.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
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
@Table(name = "episode")
@SQLDelete(sql = "UPDATE episode SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Episode {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  @ManyToOne
  @JoinColumn(name = "patient_id", referencedColumnName = "id")
  private Patient patient;
  @Column(columnDefinition = "TEXT")
  private String report;
  @OneToMany(mappedBy = "episode", fetch = FetchType.EAGER)
  private List<KcReport> kcReports;
  @Column(columnDefinition = "TEXT")
  private String decision;
  @OneToMany(mappedBy = "episode", fetch = FetchType.EAGER)
  private List<Presentation> presentations;
  @OneToOne
  @JoinColumn(name = "requirement_id", referencedColumnName = "id")
  private Requirement requirement;

  @OneToMany(mappedBy = "episode", fetch = FetchType.EAGER)
  private List<StepInfo> stepInfo;

  @ManyToOne
  @JoinColumn(name = "workflow_id", referencedColumnName = "id")
  private Workflow workflow;

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
