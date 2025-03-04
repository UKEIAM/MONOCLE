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

public class DnaFusion {

    @Id
    private String id;
    @Convert(converter = ValueSetConverter.class)
    private DnaFusionPartner fusionPartner5prime;
    @Convert(converter = ValueSetConverter.class)
    private DnaFusionPartner fusionPartner3prime;
    private Integer reportedNumReads;

}
