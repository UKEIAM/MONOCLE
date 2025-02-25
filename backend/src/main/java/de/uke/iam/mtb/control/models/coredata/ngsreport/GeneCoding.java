package de.uke.iam.mtb.control.models.coredata.ngsreport;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GeneCoding {

    private String ensemblId;
    private String hgncId;
    private String symbol;
    private String name;

}
