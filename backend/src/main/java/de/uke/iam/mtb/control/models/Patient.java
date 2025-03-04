package de.uke.iam.mtb.control.models;

import de.uke.iam.mtb.control.models.enums.GenderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patient")
@SQLDelete(sql = "UPDATE patient SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Patient {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  private String soarianId;
  private String firstName;
  private String surname;
  private GenderType gender;
  private LocalDate dateOfBirth;
  private LocalDate dateOfDeath;
  private String municipalityKey;
  private Boolean consent;
  @OneToMany(mappedBy = "patient")
  private List<Episode> episodes;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "health_insurance_id", referencedColumnName = "ik")
  private HealthInsurance healthInsurance;
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