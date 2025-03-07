package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.RebiopsyRequestDto;
import de.uke.iam.mtb.api.server.RebiopsyRequestApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.RebiopsyRequestService;
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
public class RebiopsyRequestController implements RebiopsyRequestApi {

    public RebiopsyRequestService rebiopsyRequestService;
    public AuditTrailService auditTrailService;

    public RebiopsyRequestController(RebiopsyRequestService rebiopsyRequestService, AuditTrailService auditTrailService) {
        this.rebiopsyRequestService = rebiopsyRequestService;
        this.auditTrailService = auditTrailService;
    }


    public ResponseEntity<RebiopsyRequestDto> addRebiopsyRequest(@PathVariable("episodeId") UUID episodeId,
        @Valid @RequestBody RebiopsyRequestDto rebiopsyRequestDto) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add to episode with ID " + episodeId);

        if (!episodeId.equals(rebiopsyRequestDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(rebiopsyRequestService.addRebiopsyRequest(rebiopsyRequestDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<List<RebiopsyRequestDto>> getAllRebiopsyRequests(@PathVariable("episodeId") UUID episodeId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        try {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get all by episode with ID " + episodeId);
            return ResponseEntity.ok(rebiopsyRequestService.getAllRebiopsyRequests(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    public ResponseEntity<RebiopsyRequestDto> getRebiopsyRequest(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("rebiopsyRequestId") UUID rebiopsyRequestId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by ID " + rebiopsyRequestId);

        if (!rebiopsyRequestService.isRebiopsyRequestExist(rebiopsyRequestId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, rebiopsyRequest with ID " + rebiopsyRequestId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        RebiopsyRequestDto rebiopsyRequestDto = rebiopsyRequestService.getRebiopsyRequest(rebiopsyRequestId);

        if (!episodeId.equals(rebiopsyRequestDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(rebiopsyRequestService.getRebiopsyRequest(rebiopsyRequestId));
    }

    public ResponseEntity<RebiopsyRequestDto> updateRebiopsyRequest(@PathVariable("episodeId") UUID episodeId
        , @PathVariable("rebiopsyRequestId") UUID rebiopsyRequestId, @Valid @RequestBody RebiopsyRequestDto rebiopsyRequestDto) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by ID " + rebiopsyRequestId);

        if (!rebiopsyRequestService.isRebiopsyRequestExist(rebiopsyRequestId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, rebiopsyRequest with ID " + rebiopsyRequestId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        RebiopsyRequestDto savedRebiopsyRequestDto = rebiopsyRequestService.getRebiopsyRequest(rebiopsyRequestId);

        if (!rebiopsyRequestDto.getEpisodeId().equals(savedRebiopsyRequestDto.getEpisodeId()) || !episodeId.equals(
            rebiopsyRequestDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(rebiopsyRequestService.updateRebiopsyRequest(rebiopsyRequestId, rebiopsyRequestDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    public ResponseEntity<Void> deleteRebiopsyRequest(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("rebiopsyRequestId") UUID rebiopsyRequestId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, "delete RebiopsyRequest by ID " + rebiopsyRequestId);

        if (!rebiopsyRequestService.isRebiopsyRequestExist(rebiopsyRequestId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, rebiopsyRequest with ID " + rebiopsyRequestId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            rebiopsyRequestService.deleteRebiopsyRequest(rebiopsyRequestId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed to delete RebiopsyRequest by ID " + rebiopsyRequestId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
