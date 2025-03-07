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
