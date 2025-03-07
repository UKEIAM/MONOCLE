package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.json.CodeConverter;
import de.uke.iam.mtb.control.models.converter.json.ValueSetConverter;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "molecular_therapy_response")
@SQLDelete(sql = "UPDATE molecular_therapy_response SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class MolecularTherapyResponse {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "episode_id", referencedColumnName = "id")
    private Episode episode;
    @ManyToOne
    private MolecularTherapy therapy;
    private LocalDate effectiveDate;
    @Convert(converter = CodeConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private Code method;
    @Convert(converter = ValueSetConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private ValueSet value;

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
