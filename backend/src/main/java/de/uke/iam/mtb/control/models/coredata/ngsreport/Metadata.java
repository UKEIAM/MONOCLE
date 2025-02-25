package de.uke.iam.mtb.control.models.coredata.ngsreport;

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
//@Table(name = "meta_data")
//// Hibernate will execute the SQL statement specified in the @SQLDelete annotation, which sets the deletedAt field to the current timestamp.
//@SQLDelete(sql = "UPDATE MetaData SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
//// The @Where annotation ensures that Hibernate only retrieves records that have not been marked as deleted.
//@Where(clause = "deleted_at IS NULL")

public class Metadata {

    private String kitType;
    private String kitManufacturer;
    private String sequencer;
    private String referenceGenome;
    private String pipeline;

}
