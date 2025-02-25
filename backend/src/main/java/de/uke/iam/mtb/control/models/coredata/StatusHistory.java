package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.enums.coredata.StatusHistoryStage;
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
public class StatusHistory {

  private StatusHistoryStage status;
  private LocalDate date;

}
