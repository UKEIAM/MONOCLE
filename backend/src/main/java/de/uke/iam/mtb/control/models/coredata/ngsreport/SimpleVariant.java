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
//@Entity
//@Table(name = "simple_variant")
// Hibernate will execute the SQL statement specified in the @SQLDelete annotation, which sets the deletedAt field to the current timestamp.
//@SQLDelete(sql = "UPDATE SimpleVariant SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
// The @Where annotation ensures that Hibernate only retrieves records that have not been marked as deleted.
//@Where(clause = "deleted_at IS NULL")

//Please note that this approach requires you to handle transactions manually to ensure that the delete() operation is actually executed.
// You can do this by calling the flush() method on your repository or by using the @Transactional annotation on your service methods.
public class SimpleVariant {
    @Id
    private String id;
//    @ManyToOne
//    private Episode episode;
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
