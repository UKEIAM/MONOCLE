package de.uke.iam.mtb.control.rest;

import de.uke.iam.mtb.api.model.AuditTrailEntryDto;
import de.uke.iam.mtb.api.server.AudittrailentryApi;
import de.uke.iam.mtb.control.service.AuditTrailService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuditTrailController implements AudittrailentryApi {

  private AuditTrailService auditTrailService;

  public AuditTrailController(AuditTrailService auditTrailService) {
    this.auditTrailService = auditTrailService;
  }

  @Override
  @Secured({"ROLE_MTBADMIN"})
  public ResponseEntity<List<AuditTrailEntryDto>> getAuditTrails() {
    return ResponseEntity.ok(auditTrailService.getAllEntries());
  }
}
