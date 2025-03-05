package de.uke.iam.mtb.control.models;

import de.uke.iam.mtb.control.models.enums.requirement.NgsTypeEnum;
import de.uke.iam.mtb.control.models.enums.requirement.OthersTypeEnum;
import de.uke.iam.mtb.control.models.enums.requirement.StandardTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "requirement")
@SQLDelete(sql = "UPDATE requirement SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Requirement {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  @OneToOne
  @JoinColumn(name = "episode_id", referencedColumnName = "id")
  private Episode episode;
  private Boolean recommended;
  private Boolean internDiagnostic;
  private Boolean moleculareDiagnostic;
  private Boolean ngs;
  private NgsTypeEnum ngsType;
  private Boolean standard;
  private StandardTypeEnum standardType;
  private Boolean others;
  private OthersTypeEnum othersType;
  private String comment;
  @OneToMany(mappedBy = "id")
  private List<AddressbookEntry> assignors;
  @OneToMany(mappedBy = "id")
  private List<AddressbookEntry> pathologists;
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
