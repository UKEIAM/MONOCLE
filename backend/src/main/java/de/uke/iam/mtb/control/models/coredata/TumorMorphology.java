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
public class TumorMorphology {

  private UUID id;
  private UUID episodeId;
  private UUID specimen;
  private Code value;
  private String note;

}
