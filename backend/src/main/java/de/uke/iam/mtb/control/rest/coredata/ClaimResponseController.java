package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.ClaimResponseDto;
import de.uke.iam.mtb.api.server.ClaimResponseApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.ClaimResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Secured({"ROLE_MTBDOCTOR"})
@RequestMapping("/api")
public class ClaimResponseController implements ClaimResponseApi {

    public ClaimResponseService claimResponseService;
    public AuditTrailService auditTrailService;

    public ClaimResponseController(ClaimResponseService claimResponseService, AuditTrailService auditTrailService) {
        this.claimResponseService = claimResponseService;
        this.auditTrailService = auditTrailService;
    }

    @Operation(
        operationId = "addClaimResponse",
        summary = "add claim response for an episode.",
        description = "add claim response for an episode.",
        tags = { "claim_response" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ClaimResponseDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Check episodeId and claim in your request)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/episodes/{episodeId}/claimresponses",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    public ResponseEntity<ClaimResponseDto> addClaimResponse(@PathVariable("episodeId") UUID episodeId,
        @RequestBody ClaimResponseDto claimResponseDto
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add to episode with ID " + episodeId);

        if (!episodeId.equals(claimResponseDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, Episode not found episode ID " + claimResponseDto.getEpisodeId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (claimResponseDto.getId() != null) {
            claimResponseDto.setId(null);
        }

        try {
            return ResponseEntity.ok(claimResponseService.addClaimResponse(claimResponseDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(
        operationId = "getAllClaimResponses",
        summary = "List all claim responses for an episode.",
        tags = { "claim_response" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ClaimResponseDto.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode not found)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/claimresponses",
        produces = { "application/json" }
    )
    public ResponseEntity<List<ClaimResponseDto>> getAllClaimResponses(
        @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        try {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by episode with ID " + episodeId);
            return ResponseEntity.ok(claimResponseService.getAllClaimResponses(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(
        operationId = "getClaimResponse",
        summary = "get claim response for a specific claimResponseId.",
        tags = { "claim_response" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ClaimResponseDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode not found or is not the same as the saved in the database)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "claim response not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/claimresponses/{claimResponseId}",
        produces = { "application/json" }
    )
    public ResponseEntity<ClaimResponseDto> getClaimResponse(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("claimResponseId") UUID claimResponseId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by ID " + claimResponseId);

        if (!claimResponseService.isClaimResponseExist(claimResponseId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, claim response with ID " + claimResponseId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ClaimResponseDto claimResponseDto = claimResponseService.getClaimResponse(claimResponseId);

        if (!episodeId.equals(claimResponseDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, episode ID of dto and from request are not equal");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(claimResponseService.getClaimResponse(claimResponseId));
    }

    @Operation(
        operationId = "updateClaimResponse",
        summary = "Change claim response for an episode.",
        description = "Change claim response for an episode.",
        tags = { "claim_response" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ClaimResponseDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Check episodeId and claim in your request)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "claim response not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/episodes/{episodeId}/claimresponses/{claimResponseId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    public ResponseEntity<ClaimResponseDto> updateClaimResponse(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("claimResponseId") UUID claimResponseId,
        @RequestBody ClaimResponseDto claimResponseDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by ID " + claimResponseId);

        if (!claimResponseService.isClaimResponseExist(claimResponseId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, claim response with ID " + claimResponseId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ClaimResponseDto savedClaimResponseDto = claimResponseService.getClaimResponse(claimResponseId);

        if (!claimResponseDto.getEpisodeId().equals(savedClaimResponseDto.getEpisodeId()) || !episodeId.equals(
            claimResponseDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed unequal episode Ids for episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(claimResponseService.updateClaimResponse(claimResponseId, claimResponseDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @Operation(
        operationId = "deleteClaimResponse",
        summary = "Delete claim response for an episode.",
        description = "Delete claim response for an episode.",
        tags = { "claim_response" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "claim response not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/episodes/{episodeId}/claimresponses/{claimResponseId}"
    )
    public ResponseEntity<Void> deleteClaimResponse(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("claimResponseId") UUID claimResponseId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " delete by ID " + claimResponseId);

        if (!claimResponseService.isClaimResponseExist(claimResponseId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, claim response with ID " + claimResponseId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            claimResponseService.deleteClaimResponse(claimResponseId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed to delete Claim response by ID " + claimResponseId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
