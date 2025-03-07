package de.uke.iam.mtb.control.models.coredata;

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
public class Code {

  private String code;
  private String system;
  private String display;
  private String version;

}
