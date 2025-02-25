package de.uke.iam.mtb.control.rest;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.WorkflowDto;
import de.uke.iam.mtb.api.server.WorkflowApi;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.WorkflowService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Secured({"ROLE_MTBADMIN"})
public class WorkflowController implements WorkflowApi {

  WorkflowService workflowService;
  AuditTrailService auditTrailService;

  WorkflowController(WorkflowService workflowService, AuditTrailService auditTrailService) {
    this.workflowService = workflowService;
    this.auditTrailService = auditTrailService;
  }

  @Override
  public ResponseEntity<Void> addWorkflow(@Valid @RequestBody WorkflowDto workflowDto) {
    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    try {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add with id " + workflowDto.getId() + " and number of steps " + workflowDto.getSteps().size());
      workflowService.addWorkflow(workflowDto);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @Override
  @Secured({"ROLE_MTBDOCTOR"})
  public ResponseEntity<List<WorkflowDto>> getWorkflows() {
    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName());
    return ResponseEntity.ok(workflowService.getAllWorkflows());
  }
}
