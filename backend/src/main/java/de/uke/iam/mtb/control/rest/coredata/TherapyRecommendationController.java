package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.TherapyRecommendationDto;
import de.uke.iam.mtb.api.server.TherapyRecommendationApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.TherapyRecommendationService;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
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
@Secured({"ROLE_MTBDOCTOR"})
public class TherapyRecommendationController implements TherapyRecommendationApi {

  public TherapyRecommendationService therapyRecommendationService;
  public AuditTrailService auditTrailService;

  public TherapyRecommendationController(TherapyRecommendationService therapyRecommendationService, AuditTrailService auditTrailService) {
    this.therapyRecommendationService = therapyRecommendationService;
    this.auditTrailService = auditTrailService;
  }


  public ResponseEntity<TherapyRecommendationDto> addTherapyRecommendation(@PathVariable("episodeId") UUID episodeId,
      @Valid @RequestBody TherapyRecommendationDto therapyRecommendationDto) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add to episode with ID " + episodeId);

    if (!episodeId.equals(therapyRecommendationDto.getEpisodeId())) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    if (therapyRecommendationDto.getId() != null) {
      therapyRecommendationDto.setId(null);
    }

    try {
      return ResponseEntity.ok(therapyRecommendationService.addTherapyRecommendation(therapyRecommendationDto));
    } catch (ForeignKeyException exception) {
      auditTrailService.addEntry(jwtClaimMap,
          getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  public ResponseEntity<List<TherapyRecommendationDto>> getAllTherapyRecommendations(@PathVariable("episodeId") UUID episodeId) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

    try {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get all by episode with ID " + episodeId);
      return ResponseEntity.ok(therapyRecommendationService.getAllTherapyRecommendations(episodeId));
    } catch (ForeignKeyException exception) {
        auditTrailService.addEntry(jwtClaimMap,
            getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

  }

  public ResponseEntity<TherapyRecommendationDto> getTherapyRecommendation(@PathVariable("episodeId") UUID episodeId,
      @PathVariable("therapyRecommendationId") UUID therapyRecommendationId) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by ID " + therapyRecommendationId);

    if (!therapyRecommendationService.isTherapyRecommendationExist(therapyRecommendationId)) {
        auditTrailService.addEntry(jwtClaimMap,
            getCurrentMethodName() + ", failed, TherapyRecommendation with ID " + therapyRecommendationId + " does not exist");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    TherapyRecommendationDto therapyRecommendationDto = therapyRecommendationService.getTherapyRecommendation(therapyRecommendationId);

    if (!episodeId.equals(therapyRecommendationDto.getEpisodeId())) {
        auditTrailService.addEntry(jwtClaimMap,
            getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    return ResponseEntity.ok(therapyRecommendationService.getTherapyRecommendation(therapyRecommendationId));
  }

  public ResponseEntity<TherapyRecommendationDto> updateTherapyRecommendation(@PathVariable("episodeId") UUID episodeId
      , @PathVariable("therapyRecommendationId") UUID therapyRecommendationId,
      @Valid @RequestBody TherapyRecommendationDto therapyRecommendationDto) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by ID " + therapyRecommendationId);

    if (!therapyRecommendationService.isTherapyRecommendationExist(therapyRecommendationId)) {
      auditTrailService.addEntry(jwtClaimMap,
          getCurrentMethodName() + ", failed, TherapyRecommendation with ID " + therapyRecommendationId + " does not exist");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    TherapyRecommendationDto savedTherapyRecommendationDto = therapyRecommendationService.getTherapyRecommendation(therapyRecommendationId);

    if (!therapyRecommendationDto.getEpisodeId().equals(savedTherapyRecommendationDto.getEpisodeId()) || !episodeId.equals(
        therapyRecommendationDto.getEpisodeId())) {
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, episode ID of dto and from request are not equal");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      return ResponseEntity.ok(therapyRecommendationService.updateTherapyRecommendation(therapyRecommendationId, therapyRecommendationDto));
    } catch (ForeignKeyException exception) {
        auditTrailService.addEntry(jwtClaimMap,
            getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

  }

  public ResponseEntity<Void> deleteTherapyRecommendation(@PathVariable("episodeId") UUID episodeId,
      @PathVariable("therapyRecommendationId") UUID therapyRecommendationId) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " delete by ID " + therapyRecommendationId);

    if (!therapyRecommendationService.isTherapyRecommendationExist(therapyRecommendationId)) {
        auditTrailService.addEntry(jwtClaimMap,
            getCurrentMethodName() + ", failed, TherapyRecommendation with ID " + therapyRecommendationId + " does not exist");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    try {
      therapyRecommendationService.deleteTherapyRecommendation(therapyRecommendationId);
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (IllegalStateException exception) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed to delete TherapyRecommendation by ID " + therapyRecommendationId + ". Object is referenced somewhere else");
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    } catch (Exception exception) {
        auditTrailService.addEntry(jwtClaimMap,
            getCurrentMethodName() + ", failed to delete TherapyRecommendation, with ID " + therapyRecommendationId);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

  }
}
