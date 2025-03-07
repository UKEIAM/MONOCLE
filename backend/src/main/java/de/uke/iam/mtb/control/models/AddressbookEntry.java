package de.uke.iam.mtb.control.models;

import de.uke.iam.mtb.control.models.enums.AddressbookEntryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
@Table(name = "addressbook")
@SQLDelete(sql = "UPDATE addressbook SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class AddressbookEntry {

  @Id
  @Column(nullable = false)
  private UUID id;

  private String title, firstname, lastname, email, telephone, fax, weburl, street, streetnumber, plz, location;
  private Boolean UKEinternal;
  private AddressbookEntryType addressbookEntryType;
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