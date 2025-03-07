package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.HistologyReevaluationRequestDto;
import de.uke.iam.mtb.api.server.HistologyReevaluationRequestApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.HistologyReevaluationRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Secured({"ROLE_MTBDOCTOR"})
public class HistologyReevaluationRequestController implements HistologyReevaluationRequestApi {

    public HistologyReevaluationRequestService histologyReevaluationRequestService;
    public AuditTrailService auditTrailService;

    public HistologyReevaluationRequestController(HistologyReevaluationRequestService histologyReevaluationRequestService, AuditTrailService auditTrailService) {
        this.histologyReevaluationRequestService = histologyReevaluationRequestService;
        this.auditTrailService = auditTrailService;
    }


    public ResponseEntity<HistologyReevaluationRequestDto> addHistologyReevaluationRequest(@PathVariable("episodeId") UUID episodeId,
                                                                                           @Valid @RequestBody HistologyReevaluationRequestDto histologyReevaluationRequestDto) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", add to episode with ID " + episodeId);

        if (!episodeId.equals(histologyReevaluationRequestDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(histologyReevaluationRequestService.addHistologyReevaluationRequest(histologyReevaluationRequestDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<List<HistologyReevaluationRequestDto>> getAllHistologyReevaluationRequests(@PathVariable("episodeId") UUID episodeId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        try {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get all by episode with ID " + episodeId);
            return ResponseEntity.ok(histologyReevaluationRequestService.getAllHistologyReevaluationRequests(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    public ResponseEntity<HistologyReevaluationRequestDto> getHistologyReevaluationRequest(@PathVariable("episodeId") UUID episodeId,
                                                                                           @PathVariable("histologyReevaluationRequestId") UUID histologyReevaluationRequestId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by ID " + histologyReevaluationRequestId);

        if (!histologyReevaluationRequestService.isHistologyReevaluationRequestExist(histologyReevaluationRequestId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, HistologyReevaluationRequest with ID " + histologyReevaluationRequestId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HistologyReevaluationRequestDto histologyReevaluationRequestDto = histologyReevaluationRequestService.getHistologyReevaluationRequest(histologyReevaluationRequestId);

        if (!episodeId.equals(histologyReevaluationRequestDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, episode ID of dto and from request are not equal");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(histologyReevaluationRequestDto);
    }

    public ResponseEntity<HistologyReevaluationRequestDto> updateHistologyReevaluationRequest(@PathVariable("episodeId") UUID episodeId
            , @PathVariable("histologyReevaluationRequestId") UUID histologyReevaluationRequestId, @Valid @RequestBody HistologyReevaluationRequestDto histologyReevaluationRequestDto) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by ID " + histologyReevaluationRequestId);

        if (!histologyReevaluationRequestService.isHistologyReevaluationRequestExist(histologyReevaluationRequestId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, HistologyReevaluationRequest with ID " + histologyReevaluationRequestId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HistologyReevaluationRequestDto savedHistologyReevaluationRequestDto = histologyReevaluationRequestService.getHistologyReevaluationRequest(histologyReevaluationRequestId);

        if (!histologyReevaluationRequestDto.getEpisodeId().equals(savedHistologyReevaluationRequestDto.getEpisodeId()) || !episodeId.equals(
                histologyReevaluationRequestDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed unequal episode Ids for episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(histologyReevaluationRequestService.updateHistologyReevaluationRequest(histologyReevaluationRequestId, histologyReevaluationRequestDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    public ResponseEntity<Void> deleteHistologyReevaluationRequest(@PathVariable("episodeId") UUID episodeId,
                                                                   @PathVariable("histologyReevaluationRequestId") UUID histologyReevaluationRequestId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " delete by ID " + histologyReevaluationRequestId);
        if (!histologyReevaluationRequestService.isHistologyReevaluationRequestExist(histologyReevaluationRequestId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, HistologyReevaluationRequest with ID " + histologyReevaluationRequestId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            histologyReevaluationRequestService.deleteHistologyReevaluationRequest(histologyReevaluationRequestId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed to delete HistologyReevaluationRequest by ID " + histologyReevaluationRequestId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
