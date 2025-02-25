package de.uke.iam.mtb.control.models.coredata;

import java.time.LocalDate;
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
public class NoTargetFinding {

  private UUID episodeId;
  private UUID diagnosis;
  private LocalDate issuedOn;

}
