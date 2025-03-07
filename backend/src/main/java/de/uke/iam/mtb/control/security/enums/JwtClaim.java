package de.uke.iam.mtb.control.security.enums;

public enum JwtClaim {
  USERNAME("preferred_username"),
  FIRSTNAME("given_name"),
  SURNAME("family_name"),
  NAME("name"),
  EMAIL("email"),
  SID("sid");

  private String text;

  JwtClaim(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }
}
