package de.uke.iam.mtb.control.helper;

import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.security.enums.JwtClaim;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class FullnameKeyCloakHelper {

  public String getFullnameFromKeyCloak() {
    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

    String firstname = (String) jwtClaimMap.get(JwtClaim.FIRSTNAME.getText());
    String surname = (String) jwtClaimMap.get(JwtClaim.SURNAME.getText());

    return surname + ", " + firstname;
  }

}
