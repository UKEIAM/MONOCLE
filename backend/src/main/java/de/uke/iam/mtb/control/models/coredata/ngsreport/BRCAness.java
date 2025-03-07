package de.uke.iam.mtb.control.models.coredata.ngsreport;

import java.util.UUID;
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
public class BRCAness {

    private UUID id;
    private UUID specimen;
    private float value;
    private ConfidenceRange confidenceRange;
}
