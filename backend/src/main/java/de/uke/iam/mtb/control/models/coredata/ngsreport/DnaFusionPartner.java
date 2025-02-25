package de.uke.iam.mtb.control.models.coredata.ngsreport;

import de.uke.iam.mtb.control.models.enums.coredata.ChromosomeType;
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
public class DnaFusionPartner {

    private ChromosomeType chromosome;
    private Float position;
    private GeneCoding gene;
}
