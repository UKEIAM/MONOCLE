package de.uke.iam.mtb.control.rest;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.AddEpisodeRequestDto;
import de.uke.iam.mtb.api.model.EpisodeDto;
import de.uke.iam.mtb.api.server.EpisodeApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.EpisodeService;
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
public class EpisodeController implements EpisodeApi {

  public EpisodeService episodeService;
  public AuditTrailService auditTrailService;

  public EpisodeController(EpisodeService episodeService, AuditTrailService auditTrailService) {
    this.episodeService = episodeService;
    this.auditTrailService = auditTrailService;
  }

  @Override
  @Secured({"ROLE_MTBDOCTOR"})
  public ResponseEntity<EpisodeDto> addEpisode(@RequestBody AddEpisodeRequestDto addEpisodeRequestDto) {
    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    try {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add for Patient: " + addEpisodeRequestDto.getPatientId());
      EpisodeDto savedEpisodeDto = episodeService.addEpisode(addEpisodeRequestDto.getPatientId(),
              addEpisodeRequestDto.getWorkflowId(), false);
      // TODO : By Creating a new Episode , copy the old core data of the patient to the new episode
      return ResponseEntity.ok(savedEpisodeDto);
    } catch (ForeignKeyException e) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed with ForeignKeyException");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @Override
  @Secured({"ROLE_MTBDOCTOR"})
  public ResponseEntity<EpisodeDto> getEpisode(@PathVariable("id") UUID id) {
    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by ID: " + id);
    if (!episodeService.isEpisodeExist(id)) {
      auditTrailService.addEntry(jwtClaimMap,
          getCurrentMethodName() + ", failed, episode with ID " + id + " does not exist");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } else {
      return ResponseEntity.ok(episodeService.getEpisode(id));
    }
  }

  @Override
  @Secured({"ROLE_MTBDOCTOR"})
  public ResponseEntity<EpisodeDto> updateEpisode(@PathVariable("id") UUID id,
      @Valid @RequestBody(required = false) EpisodeDto episodeDto) {
    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by ID: " + id);

    if (!episodeService.isEpisodeExist(id)) {
        auditTrailService.addEntry(jwtClaimMap,
            getCurrentMethodName() + ", failed, episode with ID " + id + " does not exist");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    if (!episodeDto.getId().equals(id)) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    try {
      EpisodeDto updatedEpisodeDto = episodeService.updateEpisode(id, episodeDto);
      return ResponseEntity.ok(updatedEpisodeDto);
    } catch (ForeignKeyException exception) {
        auditTrailService.addEntry(jwtClaimMap,
            getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + id);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }
}
