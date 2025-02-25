package de.uke.iam.mtb.control.rest;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.HealthInsuranceDto;
import de.uke.iam.mtb.api.server.HealthinsuranceApi;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.HealthInsuranceService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthInsuranceController implements HealthinsuranceApi {

  private final HealthInsuranceService healthInsuranceService;
  private final AuditTrailService auditTrailService;

  public HealthInsuranceController(HealthInsuranceService healthInsuranceService,
      AuditTrailService auditTrailService) {
    this.healthInsuranceService = healthInsuranceService;
    this.auditTrailService = auditTrailService;
  }

  @Override
  @Secured({"ROLE_MTBADMIN", "ROLE_MTBDOCTOR"})
  public ResponseEntity<List<HealthInsuranceDto>> getHealthInsurance() {
    return ResponseEntity.ok(healthInsuranceService.getAllEntries());
  }

  @Override
  @Secured({"ROLE_MTBADMIN"})
  public ResponseEntity<Void> insertDatabase(List<HealthInsuranceDto> healthInsuranceDto) {
    JwtClaimMap jwtClaimMap = new JwtClaimMap(
        (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add List of health insurances");

    healthInsuranceService.insertListOfEntries(healthInsuranceDto);
    return ResponseEntity.ok().build();
  }

  @Override
  @Secured({"ROLE_MTBADMIN", "ROLE_MTBDOCTOR"})
  public ResponseEntity<HealthInsuranceDto> getHealthInsuranceByID(Long id) {
    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by ID: " + id.toString());

    HealthInsuranceDto healthInsurance = healthInsuranceService.getHealthInsuranceByID(id);
    if (healthInsurance == null) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, no health insurance found with ID: " + id);
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(healthInsurance);
    }
  }
}
