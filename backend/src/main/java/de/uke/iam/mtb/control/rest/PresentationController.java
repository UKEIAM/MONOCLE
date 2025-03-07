package de.uke.iam.mtb.control.rest;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.PresentationDto;
import de.uke.iam.mtb.api.server.PresentationApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.PresentationService;
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
public class PresentationController implements PresentationApi {

    public PresentationService presentationService;
    public AuditTrailService auditTrailService;

    public PresentationController(PresentationService presentationService, AuditTrailService auditTrailService) {
        this.presentationService = presentationService;
        this.auditTrailService = auditTrailService;
    }

    public ResponseEntity<PresentationDto> addPresentation(@PathVariable("episodeId") UUID episodeId,
        @Valid @RequestBody PresentationDto presentationDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add presentation to episode with ID " + episodeId);

        if (presentationService.isPresentationWithDateExist(episodeId, presentationDto)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, presentation with date already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        try {
            if (!episodeId.equals(presentationDto.getEpisodeId())) {
                auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, episode ID of dto and from request are not equal");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.ok(presentationService.addPresentation(episodeId, presentationDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<List<PresentationDto>> getAllPresentations(@PathVariable("episodeId") UUID episodeId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        try {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get all by episode with ID " + episodeId);
            return ResponseEntity.ok(presentationService.getAllPresentationsByEpisodeId(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<PresentationDto> getPresentation(
        @PathVariable("episodeId") UUID episodeId, @PathVariable("presentationId") UUID presentationId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by ID: " + presentationId);

        if (!presentationService.isPresentationExist(presentationId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, presentation with ID " + presentationId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        PresentationDto presentationDto = presentationService.getPresentation(presentationId);

        if (!episodeId.equals(presentationDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, episode ID of dto and from request are not equal");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(presentationService.getPresentation(presentationId));
    }

    public ResponseEntity<PresentationDto> updatePresentation(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("presentationId") UUID presentationId, @Valid @RequestBody PresentationDto presentationDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by presentation with ID " + presentationId);

        if (!presentationService.isPresentationExist(presentationId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, presentation with ID " + presentationId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        PresentationDto savedPresentationDto = presentationService.getPresentation(presentationId);

        if (!presentationDto.getEpisodeId().equals(savedPresentationDto.getEpisodeId()) || !episodeId.equals(
            presentationDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, unequal episode Ids for episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // if date of presentation is changed and new date already exists
        if (!savedPresentationDto.getDateOfPresentation().equals(presentationDto.getDateOfPresentation()) &&
            presentationService.isPresentationWithDateExist(episodeId, presentationDto)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, presentation with date already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.ok(presentationService.updatePresentation(presentationId, presentationDto));
    }

    @Override
    public ResponseEntity<Void> deletePresentation(@PathVariable("episodeId") UUID episodeId, UUID presentationId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " delete Presentation with ID: " + presentationId.toString());

        if (presentationService.isPresentationExist(presentationId)) {
            presentationService.deletePresentation(presentationId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, presentation with ID " + presentationId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
