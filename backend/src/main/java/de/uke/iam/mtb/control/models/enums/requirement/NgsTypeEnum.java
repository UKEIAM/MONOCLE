package de.uke.iam.mtb.control.models.enums.requirement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NgsTypeEnum {
  NGS_500_PANEL("NGS 500 Panel"),

  RNA_PANEL("RNA Panel"),

  EXOM("Exom"),

  TRANSKRIPTOM("Transkriptom");

  private String value;

  NgsTypeEnum(String value) {
    this.value = value;
  }

  @JsonCreator
  public static NgsTypeEnum fromValue(String value) {
    for (NgsTypeEnum b : NgsTypeEnum.values()) {
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
