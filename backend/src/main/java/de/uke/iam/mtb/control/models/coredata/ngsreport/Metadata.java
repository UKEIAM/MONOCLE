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

public class Metadata {

    private String kitType;
    private String kitManufacturer;
    private String sequencer;
    private String referenceGenome;
    private String pipeline;

}
