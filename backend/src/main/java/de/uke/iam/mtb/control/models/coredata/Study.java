package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.enums.coredata.StudySystemEnum;
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
public class Study {

    private StudySystemEnum system;
    private String value;

}
