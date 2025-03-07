package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "rebiopsy_request")
@SQLDelete(sql = "UPDATE rebiopsy_request SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")

public class RebiopsyRequest {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "episode_id", referencedColumnName = "id")
    private Episode episode;
    @ManyToOne
    private Specimen specimen;
    private LocalDate issuedOn;
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
