package de.uke.iam.mtb.control.rest;

import de.uke.iam.mtb.api.model.RequirementDto;
import de.uke.iam.mtb.api.server.RequirementApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.RequirementService;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RequirementController implements RequirementApi {

  private RequirementService requirementService;
  private final AuditTrailService auditTrailService;

  public RequirementController(RequirementService requirementService,
      AuditTrailService auditTrailService) {
    this.requirementService = requirementService;
    this.auditTrailService = auditTrailService;
  }

  @Override
  @Secured({"ROLE_MTBDOCTOR"})
  public ResponseEntity<RequirementDto> addRequirement(@PathVariable("episodeId") UUID episodeId,
                                                       @RequestBody RequirementDto requirementDto) {
    JwtClaimMap jwtClaimMap = new JwtClaimMap(
        (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, "addRequirement: " + requirementDto.getEpisodeId().toString());
    if (requirementService.isRequirementPresentForEpisode(requirementDto.getEpisodeId())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    try {
      RequirementDto savedRequirementDto = requirementService.addRequirement(requirementDto);
      return ResponseEntity.ok(savedRequirementDto);
    } catch (ForeignKeyException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @Override
  @Secured({"ROLE_MTBDOCTOR"})
  public ResponseEntity<RequirementDto> getRequirement(@PathVariable("episodeId") UUID episodeId,
                                                       @PathVariable("id") UUID id) {
    JwtClaimMap jwtClaimMap = new JwtClaimMap(
        (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, "getEpisodeRequirement: " + id.toString());
    if (!requirementService.isRequirementExist(id)) {
      return ResponseEntity.notFound().build();
    }
    RequirementDto requirementDto = requirementService.getRequirement(id);
    if (requirementDto != null && !episodeId.equals(requirementDto.getEpisodeId())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    return ResponseEntity.ok(requirementDto);
  }

  @Override
  @Secured({"ROLE_MTBDOCTOR"})
  public ResponseEntity<RequirementDto> updateRequirement(@PathVariable("episodeId") UUID episodeId, UUID id,
                                                          RequirementDto requirementDto) {
    JwtClaimMap jwtClaimMap = new JwtClaimMap(
        (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, "updateRequirement: " + id.toString());
    if (!requirementService.isRequirementExist(id)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    if (!id.equals(requirementDto.getId())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    try {
      RequirementDto updatedRequirement = requirementService.updateRequirement(requirementDto);
      return ResponseEntity.ok(updatedRequirement);
    } catch (ForeignKeyException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

}
