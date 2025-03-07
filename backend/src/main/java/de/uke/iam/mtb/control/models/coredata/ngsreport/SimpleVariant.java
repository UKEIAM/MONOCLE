package de.uke.iam.mtb.control.models.coredata.ngsreport;

import de.uke.iam.mtb.control.models.converter.GeneCodingListConverter;
import de.uke.iam.mtb.control.models.converter.StartEndRangeConverter;
import de.uke.iam.mtb.control.models.converter.ValueSetConverter;
import de.uke.iam.mtb.control.models.coredata.ValueSet;
import de.uke.iam.mtb.control.models.enums.coredata.ChromosomeType;
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
public class SimpleVariant {
    @Id
    private String id;
    private ChromosomeType chromosome;
    @Convert(converter = GeneCodingListConverter.class)
    private GeneCoding gene;
    @Convert(converter = StartEndRangeConverter.class)
    private StartEndRange startEnd;
    private String refAllele;
    private String altAllele;
    @Convert(converter = ValueSetConverter.class)
    private ValueSet dnaChange;
    @Convert(converter = ValueSetConverter.class)
    private ValueSet aminoAcidChange;
    private Integer readDepth;
    private Float allelicFrequency;
    private String cosmicid;
    private String dbSNPId;
    @Convert(converter = ValueSetConverter.class)
    private ValueSet interpretation;

}
