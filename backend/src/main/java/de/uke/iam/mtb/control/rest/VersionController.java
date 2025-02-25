package de.uke.iam.mtb.control.rest;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.server.VersionApi;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.VersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VersionController implements VersionApi {

  private final VersionService versionService;
  private final AuditTrailService auditTrailService;

  public VersionController(VersionService versionService, AuditTrailService auditTrailService) {
    this.versionService = versionService;
    this.auditTrailService = auditTrailService;
  }

  @Override
  @Secured({"ROLE_MTBADMIN"})
  public ResponseEntity<String> getVersion() {
    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName());
    return ResponseEntity.ok(versionService.getVersionFromPOM());
  }
}
