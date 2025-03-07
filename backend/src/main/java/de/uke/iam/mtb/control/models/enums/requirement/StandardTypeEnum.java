package de.uke.iam.mtb.control.models.enums.requirement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StandardTypeEnum {
  SANGER("Sanger"),

  RT_PCR("RT-PCR"),

  Q_PCR("q-PCR");

  private String value;

  StandardTypeEnum(String value) {
    this.value = value;
  }

  @JsonCreator
  public static StandardTypeEnum fromValue(String value) {
    for (StandardTypeEnum b : StandardTypeEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

}
