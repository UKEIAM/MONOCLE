package de.uke.iam.mtb.control.security;

import de.uke.iam.mtb.control.security.enums.JwtClaim;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtClaimMap {

  private HashMap<String, Object> claimMap;

  private Logger logger = LoggerFactory.getLogger(JwtClaimMap.class);

  public JwtClaimMap(Jwt jwt) {
    logger.debug("JWT: {}", jwt);

    for (String s : jwt.getClaims().keySet()) {
      logger.debug("Claim: {} -> {}", s, jwt.getClaims().get(s));
    }

    claimMap = new HashMap<>();
    for (String key : jwt.getClaims().keySet()) {
      claimMap.put(key, jwt.getClaims().get(key));
    }
  }

  public HashMap<String, Object> getClaimMap() {
    return claimMap;
  }

  public Object get(String key) {
    return claimMap.get(key);
  }

  public Object get(JwtClaim jwtClaim) {
    return claimMap.get(jwtClaim.getText());
  }
}
