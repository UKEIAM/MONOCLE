package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.json.CodeConverter;
import de.uke.iam.mtb.control.models.converter.json.NoTargetFindingConverter;
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
@Table(name = "care_plan")
@SQLDelete(sql = "UPDATE care_plan SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class CarePlan {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  @ManyToOne
  @JoinColumn(name = "episode_id", referencedColumnName = "id")
  private Episode episode;
  @ManyToOne
  private Diagnose diagnosis;
  private LocalDate issuedOn;
  @Column(columnDefinition = "TEXT")
  private String description;
  @Convert(converter = NoTargetFindingConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private NoTargetFinding noTargetFinding;
  // Todo: this object should be a replacement for noTargetFinding
  @Convert(converter = CodeConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private Code statusReason;
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "care_plan_therapy_recommendation", // specify the name of the join table here
      joinColumns = @JoinColumn(name = "care_plan_id"), // specify the name of the column in the join table that references the current entity
      inverseJoinColumns = @JoinColumn(name = "therapy_recommendation_id") // specify the name of the column in the join table that
      // references the other entity
  )
  private List<TherapyRecommendation> recommendations;
  @ManyToOne
  private GeneticCounsellingRequest geneticCounsellingRequest;
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "care_plan_rebiopsy_request", // specify the name of the join table here
      joinColumns = @JoinColumn(name = "care_plan_id"), // specify the name of the column in the join table that references the current entity
      inverseJoinColumns = @JoinColumn(name = "rebiopsy_request_id") // specify the name of the column in the join table that references
      // the other entity
  )
  private List<RebiopsyRequest> rebiopsyRequests;
  @ManyToMany
  @JoinTable(
      name = "care_plan_study_inclusion_request", // specify the name of the join table here
      joinColumns = @JoinColumn(name = "care_plan_id"), // specify the name of the column in the join table that references the current entity
      inverseJoinColumns = @JoinColumn(name = "study_inclusion_request") // specify the name of the column in the join table that
      // references the other entity
  )
  private List<StudyInclusionRequest> studyInclusionRequests;

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
