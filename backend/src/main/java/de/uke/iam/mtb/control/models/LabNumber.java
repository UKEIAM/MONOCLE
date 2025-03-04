package de.uke.iam.mtb.control.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "lab_number")
public class LabNumber {

  @Id
  private String id;
  @Column(updatable = false)
  private Instant createdAt;
  private String specimenLabelling;
  private Boolean assigned;
  private Instant assignedOn;

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
  }
}