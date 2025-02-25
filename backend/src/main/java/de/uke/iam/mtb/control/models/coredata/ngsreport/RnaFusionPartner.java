package de.uke.iam.mtb.control.models.coredata.ngsreport;

import de.uke.iam.mtb.control.models.enums.coredata.ChromosomeType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RnaFusionPartner {

    private GeneCoding gene;
    private String transcriptId;
    private String exon;
    private Float position;
    private String strand;

}
