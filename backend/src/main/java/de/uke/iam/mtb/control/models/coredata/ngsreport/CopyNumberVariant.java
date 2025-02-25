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
//@Entity
//@Table(name = "copy_number_variant")
// Hibernate will execute the SQL statement specified in the @SQLDelete annotation, which sets the deletedAt field to the current timestamp.
//@SQLDelete(sql = "UPDATE CopyNumberVariant SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
// The @Where annotation ensures that Hibernate only retrieves records that have not been marked as deleted.
//@Where(clause = "deleted_at IS NULL")

//Please note that this approach requires you to handle transactions manually to ensure that the delete() operation is actually executed.
// You can do this by calling the flush() method on your repository or by using the @Transactional annotation on your service methods.
public class CopyNumberVariant {

    @Id
    private String id;
    //    @ManyToOne
//    private Episode episode;
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
