package de.uke.iam.mtb.control.models.enums.requirement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OthersTypeEnum {
  MULTIPLEX_IHC("Multiplex IHC"),

  METHYLIERUNGSANALYTIK("Methylierungsanalytik");

  private String value;

  OthersTypeEnum(String value) {
    this.value = value;
  }

  @JsonCreator
  public static OthersTypeEnum fromValue(String value) {
    for (OthersTypeEnum b : OthersTypeEnum.values()) {
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
