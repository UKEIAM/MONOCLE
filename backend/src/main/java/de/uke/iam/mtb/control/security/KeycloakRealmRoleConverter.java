package de.uke.iam.mtb.control.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  Logger logger = LoggerFactory.getLogger(KeycloakRealmRoleConverter.class);

  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

    Collection<GrantedAuthority> moo = ((List<String>) realmAccess.get("roles")).stream()
      .map(roleName -> "ROLE_" + roleName) // prefix to map to a Spring Security "role"
      .map(SimpleGrantedAuthority::new)
      .collect(Collectors.toList());

    for (GrantedAuthority grantedAuthority : moo) {
      logger.debug("User-role: {}", grantedAuthority.toString());
    }

    return moo;
  }
}