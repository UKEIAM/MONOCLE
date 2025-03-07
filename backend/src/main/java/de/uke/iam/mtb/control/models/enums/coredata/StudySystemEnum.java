package de.uke.iam.mtb.control.models.enums.coredata;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StudySystemEnum {
  NCT,
  DRKS,
  @JsonProperty("Eudra-CT") EUDRA_CT,
  @JsonProperty("other") OTHER,
  @JsonProperty("ja aber Studie nicht bekannt") JA_ABER_STUDIE_NICHT_BEKANNT;
}
