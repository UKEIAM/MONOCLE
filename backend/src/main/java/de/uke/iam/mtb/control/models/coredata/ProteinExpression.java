package de.uke.iam.mtb.control.models.coredata;

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
public class ProteinExpression {
    private UUID id;
    private Code protein;
    private Code value;
    private Integer tpsScore;
    private Integer cpsScore;
    private Code icScore;
    private Code tcScore;
}
