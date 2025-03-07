package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.enums.coredata.SpecimenLocalization;
import de.uke.iam.mtb.control.models.enums.coredata.SpecimenMethod;
import java.time.LocalDate;
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
public class SpecimenCollection {

  private SpecimenLocalization localization;
  private LocalDate date;
  private SpecimenMethod method;

}
