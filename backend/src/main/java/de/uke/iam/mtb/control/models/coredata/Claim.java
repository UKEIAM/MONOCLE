package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.json.CodeConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;
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
@Table(name = "claim")
@SQLDelete(sql = "UPDATE claim SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Claim {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "episode_id", referencedColumnName = "id")
    private Episode episode;
    private LocalDate issuedOn;
    @ManyToOne
    private TherapyRecommendation therapyRecommendation;
    private Boolean isClaimViaZpmOffice;
    @Convert(converter = CodeConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private Code stage;

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
