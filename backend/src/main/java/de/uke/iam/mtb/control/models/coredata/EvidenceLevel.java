package de.uke.iam.mtb.control.models.coredata;

import java.util.List;
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
public class EvidenceLevel {

  private ValueSet grading;
  private List<ValueSet> addendums;
  private List<Publication> publications;
}
