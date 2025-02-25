package de.uke.iam.mtb.control.models.coredata.ngsreport;

import de.uke.iam.mtb.control.models.converter.ValueSetConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Id;
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
//@Entity
//@Table(name = "rna_fusion")
// Hibernate will execute the SQL statement specified in the @SQLDelete annotation, which sets the deletedAt field to the current timestamp.
//@SQLDelete(sql = "UPDATE RnaFusion SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
// The @Where annotation ensures that Hibernate only retrieves records that have not been marked as deleted.
//@Where(clause = "deleted_at IS NULL")

public class RnaSequence {

    @Id
    private String id;
    private String entrezId;
    private String ensemblId;
    @Convert(converter = ValueSetConverter.class)
    private GeneCoding gene;
    private String transcriptId;
    private Float fragmentsPerKilobaseMillion;
    private Boolean fromNGS;
    private Boolean tissueCorrectedExpression;
    private Integer rawCounts;
    private Integer librarySize;
    private Integer cohortRanking;

}
