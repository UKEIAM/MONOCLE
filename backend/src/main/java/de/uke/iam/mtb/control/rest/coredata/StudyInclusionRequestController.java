package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.StudyInclusionRequestDto;
import de.uke.iam.mtb.api.server.StudyInclusionRequestApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.StudyInclusionRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class StudyInclusionRequestController implements StudyInclusionRequestApi {

    public StudyInclusionRequestService studyInclusionRequestService;
    public AuditTrailService auditTrailService;

    public StudyInclusionRequestController(StudyInclusionRequestService studyInclusionRequestService, AuditTrailService auditTrailService) {
        this.studyInclusionRequestService = studyInclusionRequestService;
        this.auditTrailService = auditTrailService;
    }


    @Operation(
        operationId = "addStudyInclusionRequest",
        summary = "add study inclusion request for an episode.",
        description = "add study inclusion request for an episode.",
        tags = { "study_inclusion_request" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = StudyInclusionRequestDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Check episodeId and reason (diagnoseId) in your request)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "405", description = "Invalid Input (The given episode Id in the body is not the same as in the path)")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/episodes/{episodeId}/studyinclusionrequests",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    public ResponseEntity<StudyInclusionRequestDto> addStudyInclusionRequest(@PathVariable("episodeId") UUID episodeId,
                                                                 @Valid @RequestBody StudyInclusionRequestDto studyInclusionRequestDto) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add to episode with ID " + episodeId);

        if (!episodeId.equals(studyInclusionRequestDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, "Mismatch episode with ID " + episodeId);
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        try {
            auditTrailService.addEntry(jwtClaimMap, "add StudyInclusionRequest to episode with ID " + episodeId);
            return ResponseEntity.ok(studyInclusionRequestService.addStudyInclusionRequest(studyInclusionRequestDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap, "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(
        operationId = "getAllStudyInclusionRequests",
        summary = "List all study inclusion requests for an episode.",
        tags = { "study_inclusion_request" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StudyInclusionRequestDto.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode not found)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/studyinclusionrequests",
        produces = { "application/json" }
    )
    public ResponseEntity<List<StudyInclusionRequestDto>> getAllStudyInclusionRequests(@PathVariable("episodeId") UUID episodeId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add to episode with ID " + episodeId);

        try {
            auditTrailService.addEntry(jwtClaimMap, "get all StudyInclusionRequests by episode with ID " + episodeId);
            return ResponseEntity.ok(studyInclusionRequestService.getAllStudyInclusionRequests(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap, "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @Operation(
        operationId = "getStudyInclusionRequest",
        summary = "get study inclusion request for a specific StudyInclusionRequestId.",
        tags = { "study_inclusion_request" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = StudyInclusionRequestDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode not found or is not the same as the saved in the database)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "study inclusion request not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/studyinclusionrequests/{studyInclusionRequestId}",
        produces = { "application/json" }
    )
    public ResponseEntity<StudyInclusionRequestDto> getStudyInclusionRequest(@PathVariable("episodeId") UUID episodeId,
                                                                 @PathVariable("studyInclusionRequestId") UUID studyInclusionRequestId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add to episode with ID " + episodeId);

        if (!studyInclusionRequestService.isStudyInclusionRequestExist(studyInclusionRequestId)) {
            auditTrailService.addEntry(jwtClaimMap, "StudyInclusionRequest not found " + studyInclusionRequestId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        StudyInclusionRequestDto studyInclusionRequestDto = studyInclusionRequestService.getStudyInclusionRequest(studyInclusionRequestId);

        if (!episodeId.equals(studyInclusionRequestDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, "Mismatch episode with ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        auditTrailService.addEntry(jwtClaimMap, "get StudyInclusionRequest by ID " + studyInclusionRequestId);

        return ResponseEntity.ok(studyInclusionRequestService.getStudyInclusionRequest(studyInclusionRequestId));
    }

    @Operation(
        operationId = "updateStudyInclusionRequest",
        summary = "Change study inclusion request for an episode.",
        description = "Change study inclusion request for an episode.",
        tags = { "study_inclusion_request" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = StudyInclusionRequestDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Check EpisodeId and reason)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "study inclusion request not found"),
            @ApiResponse(responseCode = "405", description = "Invalid Input")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/episodes/{episodeId}/studyinclusionrequests/{studyInclusionRequestId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    public ResponseEntity<StudyInclusionRequestDto> updateStudyInclusionRequest(@PathVariable("episodeId") UUID episodeId
            , @PathVariable("studyInclusionRequestId") UUID studyInclusionRequestId, @Valid @RequestBody StudyInclusionRequestDto studyInclusionRequestDto) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add to episode with ID " + episodeId);

        if (!studyInclusionRequestService.isStudyInclusionRequestExist(studyInclusionRequestId)) {
            auditTrailService.addEntry(jwtClaimMap, "StudyInclusionRequest not found " + studyInclusionRequestId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        StudyInclusionRequestDto savedStudyInclusionRequestDto = studyInclusionRequestService.getStudyInclusionRequest(studyInclusionRequestId);

        if (!studyInclusionRequestDto.getEpisodeId().equals(savedStudyInclusionRequestDto.getEpisodeId()) || !episodeId.equals(
                studyInclusionRequestDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, "Mismatch episode with ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            auditTrailService.addEntry(jwtClaimMap, "update StudyInclusionRequest by ID " + studyInclusionRequestId);
            return ResponseEntity.ok(studyInclusionRequestService.updateStudyInclusionRequest(studyInclusionRequestId, studyInclusionRequestDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap, "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @Operation(
        operationId = "deleteStudyInclusionRequest",
        summary = "Delete study inclusion request for an episode.",
        description = "Delete study inclusion request for an episode.",
        tags = { "study_inclusion_request" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "study inclusion request not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/episodes/{episodeId}/studyinclusionrequests/{studyInclusionRequestId}"
    )
    public ResponseEntity<Void> deleteStudyInclusionRequest(@PathVariable("episodeId") UUID episodeId,
                                                      @PathVariable("studyInclusionRequestId") UUID studyInclusionRequestId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add to episode with ID " + episodeId);
        
        if (!studyInclusionRequestService.isStudyInclusionRequestExist(studyInclusionRequestId)) {
            auditTrailService.addEntry(jwtClaimMap, "StudyInclusionRequest not found " + studyInclusionRequestId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            studyInclusionRequestService.deleteStudyInclusionRequest(studyInclusionRequestId);
            auditTrailService.addEntry(jwtClaimMap, "delete StudyInclusionRequest by ID " + studyInclusionRequestId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap, "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }
}
