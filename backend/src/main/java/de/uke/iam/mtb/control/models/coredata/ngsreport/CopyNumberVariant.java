package de.uke.iam.mtb.control.models.coredata.ngsreport;

import de.uke.iam.mtb.control.models.converter.GeneCodingListConverter;
import de.uke.iam.mtb.control.models.converter.StartEndRangeConverter;
import de.uke.iam.mtb.control.models.enums.coredata.ChromosomeType;
import de.uke.iam.mtb.control.models.enums.coredata.CopyNumberVariantType;
import jakarta.persistence.Convert;
import jakarta.persistence.Id;
import java.util.List;
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
public class CopyNumberVariant {

    @Id
    private String id;
    private ChromosomeType chromosome;
    @Convert(converter = StartEndRangeConverter.class)
    private StartEndRange startRange;
    @Convert(converter = StartEndRangeConverter.class)
    private StartEndRange endRange;
    private Integer totalCopyNumber;
    private Float relativeCopyNumber;
    private Float cnA;
    private Float cnB;
    @Convert(converter = GeneCodingListConverter.class)
    private List<GeneCoding> reportedAffectedGenes;
    private String reportedFocality;
    private CopyNumberVariantType type;
    @Convert(converter = GeneCodingListConverter.class)
    private List<GeneCoding> copyNumberNeutralLoH;

}
