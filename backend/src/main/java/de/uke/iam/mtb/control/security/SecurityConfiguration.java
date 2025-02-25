package de.uke.iam.mtb.control.security;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
  prePostEnabled = true,  // Enables @PreAuthorize and @PostAuthorize
  securedEnabled = true, // Enables @Secured
  jsr250Enabled = true    // Enables @RolesAllowed (Ensures JSR-250 annotations are enabled)
)
public class SecurityConfiguration {

  // Whitelist of all services that are permitted by default
  private static final String[] AUTH_WHITELIST = {
    "/error",
    "/whatever/whereever/**"
  };

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .cors().and()
      .csrf().disable()
      .authorizeHttpRequests()
      .requestMatchers(HttpMethod.OPTIONS).permitAll()
      .requestMatchers(AUTH_WHITELIST).permitAll()
// In case access shall be checked/blocked on this level
//      .requestMatchers(HttpMethod.GET, "/test/admin").hasRole(MtbRole.MTBADMIN.name())
//      .requestMatchers(HttpMethod.GET, "/api/form/**").hasRole(MDTADMIN)
      .anyRequest().authenticated()
      .and()
      .oauth2ResourceServer(
        oauth2ResourceServer -> oauth2ResourceServer.jwt(
          jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
      );
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    return http.build();
  }

  private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
    jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
    return jwtConverter;
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
