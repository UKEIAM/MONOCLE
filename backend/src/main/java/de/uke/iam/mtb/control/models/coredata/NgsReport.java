package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.converter.CopyNumberVariantListConverter;
import de.uke.iam.mtb.control.models.converter.DnaFusionListConverter;
import de.uke.iam.mtb.control.models.converter.MetadataListConverter;
import de.uke.iam.mtb.control.models.converter.RnaFusionListConverter;
import de.uke.iam.mtb.control.models.converter.RnaSequenceListConverter;
import de.uke.iam.mtb.control.models.converter.SimpleVariantListConverter;
import de.uke.iam.mtb.control.models.converter.TumorCellContentConverter;
import de.uke.iam.mtb.control.models.converter.json.BRCAnessConverter;
import de.uke.iam.mtb.control.models.converter.json.MicroSatelliteInstabilitiesConverter;
import de.uke.iam.mtb.control.models.converter.json.InterpretationFindingsObjectConverter;
import de.uke.iam.mtb.control.models.coredata.ngsreport.BRCAness;
import de.uke.iam.mtb.control.models.coredata.ngsreport.CopyNumberVariant;
import de.uke.iam.mtb.control.models.coredata.ngsreport.DnaFusion;
import de.uke.iam.mtb.control.models.coredata.ngsreport.Metadata;
import de.uke.iam.mtb.control.models.coredata.ngsreport.MicroSatelliteInstabilities;
import de.uke.iam.mtb.control.models.coredata.ngsreport.RnaFusion;
import de.uke.iam.mtb.control.models.coredata.ngsreport.RnaSequence;
import de.uke.iam.mtb.control.models.coredata.ngsreport.SimpleVariant;
import de.uke.iam.mtb.control.models.coredata.ngsreport.InterpretationFindingsObject;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
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
@Table(name = "ngs_report")
// Hibernate will execute the SQL statement specified in the @SQLDelete annotation, which sets the deletedAt field to the current timestamp.
@SQLDelete(sql = "UPDATE ngs_report SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
// The @Where annotation ensures that Hibernate only retrieves records that have not been marked as deleted.
@Where(clause = "deleted_at IS NULL")
public class NgsReport {

    @Id
    private String id;
    @ManyToOne
    private Episode episode;
    @ManyToOne
    private Specimen specimen;
    private LocalDate issueDate;
    private String sequencingType;
    @Convert(converter = MetadataListConverter.class)
    private List<Metadata> metadata;
    @Convert(converter = TumorCellContentConverter.class)
    private TumorCellContent tumorCellContent;
    @Convert(converter = InterpretationFindingsObjectConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private InterpretationFindingsObject tmb;
    @Convert(converter = BRCAnessConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private BRCAness brcaness;
    @Convert(converter = MicroSatelliteInstabilitiesConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private MicroSatelliteInstabilities msi;
    @Convert(converter = InterpretationFindingsObjectConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private InterpretationFindingsObject hrdScore;
    @Convert(converter = SimpleVariantListConverter.class)
    private List<SimpleVariant> simpleVariants;
    @Convert(converter = CopyNumberVariantListConverter.class)
    private List<CopyNumberVariant> copyNumberVariants;
    @Convert(converter = DnaFusionListConverter.class)
    private List<DnaFusion> dnaFusions;
    @Convert(converter = RnaFusionListConverter.class)
    private List<RnaFusion> rnaFusions;
    @Convert(converter = RnaSequenceListConverter.class)
    private List<RnaSequence> rnaSeqs;
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
