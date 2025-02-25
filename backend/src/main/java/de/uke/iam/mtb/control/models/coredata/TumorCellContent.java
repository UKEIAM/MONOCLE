package de.uke.iam.mtb.control.models.coredata;

import de.uke.iam.mtb.control.models.enums.coredata.TumorCellContentMethod;
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
public class TumorCellContent {

  private UUID id;
  private UUID specimen;
  private TumorCellContentMethod method;
  private Float value;

}
