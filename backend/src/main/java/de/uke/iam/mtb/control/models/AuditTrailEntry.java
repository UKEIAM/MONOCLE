package de.uke.iam.mtb.control.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audittrail")
public class AuditTrailEntry {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;

  private Instant dateOfEntry;

  private String userId;

  private String entry;

}
