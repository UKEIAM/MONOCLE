package de.uke.iam.mtb.control.models.coredata;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.json.ProteinExpressionListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "IHC_REPORT")
@SQLDelete(sql = "UPDATE IHC_REPORT SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class IhcReport {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "episode_id", referencedColumnName = "id")
    private Episode episode;
    @ManyToOne
    @JoinColumn(name = "specimen_id", referencedColumnName = "id")
    private Specimen specimen;

    @Convert(converter = ProteinExpressionListConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private List<ProteinExpression> proteinExpressionResults;
    @Convert(converter = ProteinExpressionListConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private List<ProteinExpression> msiMmrResults;

    private LocalDate date;
    private String journalId;
    private String blockId;

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
