package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.MolecularTherapyResponseDto;
import de.uke.iam.mtb.api.server.MolecularTherapyResponseApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.EpisodeService;
import de.uke.iam.mtb.control.service.coredata.MolecularTherapyResponseService;
import de.uke.iam.mtb.control.service.coredata.MolecularTherapyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MolecularTherapyResponseController implements MolecularTherapyResponseApi {

    public MolecularTherapyResponseService molecularTherapyResponseService;
    public AuditTrailService auditTrailService;
    public MolecularTherapyService molecularTherapyService;
    public EpisodeService episodeService;

    public MolecularTherapyResponseController(MolecularTherapyResponseService molecularTherapyResponseService, AuditTrailService auditTrailService, MolecularTherapyService molecularTherapyService,  EpisodeService episodeService) {
        this.molecularTherapyResponseService = molecularTherapyResponseService;
        this.auditTrailService = auditTrailService;
        this.episodeService = episodeService;
        this.molecularTherapyService = molecularTherapyService;
    }

    @Operation(
        operationId = "addMolecularTherapyResponse",
        summary = "add molecularTherapyResponse for an episode.",
        description = "add molecularTherapyResponse for an episode.",
        tags = { "molecular_therapy_response" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = MolecularTherapyResponseDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/episodes/{episodeId}/moleculartherapyresponses",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    public ResponseEntity<MolecularTherapyResponseDto> addMolecularTherapyResponse(@PathVariable("episodeId") UUID episodeId,
                                                                                   @RequestBody MolecularTherapyResponseDto molecularTherapyResponseDto
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " with episodeID " + episodeId);

        // check for episodeId mismatch
        if (!episodeId.equals(molecularTherapyResponseDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + " failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // check for unknown episodeId
        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + " failed, episode with ID " + episodeId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // check for unknown molecularTherapyId
        if (!molecularTherapyService.isMolecularTherapyExist(molecularTherapyResponseDto.getTherapy())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() 
                    + " failed, molecularTherapy with ID " + molecularTherapyResponseDto.getTherapy() + " does not exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // enforce id to be null on post
        if (molecularTherapyResponseDto.getId() != null) {
            molecularTherapyResponseDto.setId(null);
        }

        try {
            return ResponseEntity.ok(molecularTherapyResponseService.addMolecularTherapyResponse(molecularTherapyResponseDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                    getCurrentMethodName() + " failed, unhandled ForeignKeyException");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        operationId = "getAllMolecularTherapyResponses",
        summary = "List all moleculartherapyresponses for an episode.",
        tags = { "molecular_therapy_response" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MolecularTherapyResponseDto.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode not found)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/moleculartherapyresponses",
        produces = { "application/json" }
    )
    public ResponseEntity<List<MolecularTherapyResponseDto>> getAllMolecularTherapyResponses(
            @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " with episodeID " + episodeId);

        // check for unknown episodeId
        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + " failed, episode with ID " + episodeId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            return ResponseEntity.ok(molecularTherapyResponseService.getAllMolecularTherapyResponses(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                    getCurrentMethodName() + " failed, unhandled ForeignKeyException");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @Operation(
        operationId = "getMolecularTherapyResponse",
        summary = "get molecularTherapyResponse for a specific molecularTherapyResponseId.",
        tags = { "molecular_therapy_response" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = MolecularTherapyResponseDto.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "molecularTherapyResponse not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/moleculartherapyresponses/{molecularTherapyResponseId}",
        produces = { "application/json" }
    )
    public ResponseEntity<MolecularTherapyResponseDto> getMolecularTherapyResponse(@PathVariable("episodeId") UUID episodeId,
                                                                                   @PathVariable("molecularTherapyResponseId") UUID molecularTherapyResponseId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " with episodeID " + episodeId);

        // check for unknown episodeId
        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + " failed, episode with ID " + episodeId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // check for unknown molecularTherapyResponseId
        if (!molecularTherapyResponseService.isMolecularTherapyResponseExist(molecularTherapyResponseId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + ", failed, molecularTherapyResponse with ID " + molecularTherapyResponseId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        MolecularTherapyResponseDto molecularTherapyResponseDto = molecularTherapyResponseService.getMolecularTherapyResponse(molecularTherapyResponseId);

        // check for episodeId mismatch 
        if (!episodeId.equals(molecularTherapyResponseDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, Episode ID mismatch " + episodeId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(molecularTherapyResponseService.getMolecularTherapyResponse(molecularTherapyResponseId));
    }

    @Operation(
        operationId = "updateMolecularTherapyResponse",
        summary = "Change molecularTherapyResponse for an episode.",
        description = "Change molecularTherapyResponse for an episode.",
        tags = { "molecular_therapy_response" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = MolecularTherapyResponseDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/episodes/{episodeId}/moleculartherapyresponses/{molecularTherapyResponseId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    public ResponseEntity<MolecularTherapyResponseDto> updateMolecularTherapyResponse(@PathVariable("episodeId") UUID episodeId,
                                                                                      @PathVariable("molecularTherapyResponseId") UUID molecularTherapyResponseId,
                                                                                      @RequestBody MolecularTherapyResponseDto molecularTherapyResponseDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " with episodeID " + episodeId);

        // check for unknown episodeId
        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + " failed, episode with ID " + episodeId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // check for unknown molecularTherapyResponseId
        if (!molecularTherapyResponseService.isMolecularTherapyResponseExist(molecularTherapyResponseId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + ", failed, molecularTherapyResponse with ID " + molecularTherapyResponseId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // check for episodeId mismatch between path and request body 
        if (!episodeId.equals(molecularTherapyResponseDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, Episode ID mismatch" + molecularTherapyResponseDto.getEpisodeId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        MolecularTherapyResponseDto savedMolecularTherapyResponseDto = molecularTherapyResponseService.getMolecularTherapyResponse(molecularTherapyResponseId);

        // check for episodeId mismatch between path and database 
        if (!episodeId.equals(savedMolecularTherapyResponseDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, Episode ID mismatch" + savedMolecularTherapyResponseDto.getEpisodeId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            return ResponseEntity.ok(molecularTherapyResponseService.updateMolecularTherapyResponse(molecularTherapyResponseId, molecularTherapyResponseDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                    getCurrentMethodName() + " failed, unhandled ForeignKeyException");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        operationId = "deleteMolecularTherapyResponse",
        summary = "Delete molecularTherapyResponse for an episode.",
        description = "Delete molecularTherapyResponse for an episode.",
        tags = { "molecular_therapy_response" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/episodes/{episodeId}/moleculartherapyresponses/{molecularTherapyResponseId}"
    )
    public ResponseEntity<Void> deleteMolecularTherapyResponse(@PathVariable("episodeId") UUID episodeId,
                                                               @PathVariable("molecularTherapyResponseId") UUID molecularTherapyResponseId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " with episodeID " + episodeId);

        // check for unknown episodeId
        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + " failed, episode with ID " + episodeId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // check for unknown molecularTherapyResponseId
        if (!molecularTherapyResponseService.isMolecularTherapyResponseExist(molecularTherapyResponseId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + ", failed, molecularTherapyResponse with ID " + molecularTherapyResponseId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }


        MolecularTherapyResponseDto savedMolecularTherapyResponseDto = molecularTherapyResponseService.getMolecularTherapyResponse(molecularTherapyResponseId);

        // check for episodeId mismatch between path and database 
        if (!episodeId.equals(savedMolecularTherapyResponseDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, Episode ID mismatch" + savedMolecularTherapyResponseDto.getEpisodeId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            molecularTherapyResponseService.deleteMolecularTherapyResponse(molecularTherapyResponseId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap,
                    getCurrentMethodName() + " failed, unhandled Exception");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
