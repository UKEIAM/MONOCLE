package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.MolecularPathologyFindingDto;
import de.uke.iam.mtb.api.server.MolecularPathologyFindingApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.MolecularPathologyFindingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MolecularPathologyFindingController implements MolecularPathologyFindingApi {

    public MolecularPathologyFindingService molecularPathologyFindingService;
    public AuditTrailService auditTrailService;

    public MolecularPathologyFindingController(MolecularPathologyFindingService molecularPathologyFindingService,
        AuditTrailService auditTrailService) {
        this.molecularPathologyFindingService = molecularPathologyFindingService;
        this.auditTrailService = auditTrailService;
    }

    @Operation(
        operationId = "addMolecularPathologyFinding",
        summary = "add molecular pathology finding for an episode.",
        description = "add molecular pathology finding for an episode.",
        tags = {"molecular_pathology_finding"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = MolecularPathologyFindingDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Check episodeId and specimen in your request)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "405", description = "Invalid Input (The given episode Id in the body is not the same as in the path)")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/episodes/{episodeId}/molecularpathologyfindings",
        produces = {"application/json"},
        consumes = {"application/json"}
    )
    public ResponseEntity<MolecularPathologyFindingDto> addMolecularPathologyFinding(@PathVariable("episodeId") UUID episodeId,
        @Valid @RequestBody MolecularPathologyFindingDto molecularPathologyFindingDto) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        auditTrailService.addEntry(jwtClaimMap,
            String.format("Method %s invoked to add a molecular pathology finding to episode with ID: %s", getCurrentMethodName(),
                episodeId));

        if (!episodeId.equals(molecularPathologyFindingDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap,
                "Validation Error: The episode ID in the request body does not match the episode ID in the path.");
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        try {
            return ResponseEntity.ok(molecularPathologyFindingService.addMolecularPathologyFinding(molecularPathologyFindingDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap, "Data Integrity Violation: Foreign key constraint error occurred. Verify episodeId.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(
        operationId = "getAllMolecularPathologyFindings",
        summary = "List all molecular pathology findings for an episode.",
        tags = {"molecular_pathology_finding"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MolecularPathologyFindingDto.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode not found)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/molecularpathologyfindings",
        produces = {"application/json"}
    )
    public ResponseEntity<List<MolecularPathologyFindingDto>> getAllMolecularPathologyFindings(@PathVariable("episodeId") UUID episodeId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        auditTrailService.addEntry(jwtClaimMap,
            String.format("Method %s invoked to retrieve all molecular pathology findings for episode with ID: %s", getCurrentMethodName(),
                episodeId));

        try {
            return ResponseEntity.ok(molecularPathologyFindingService.getAllMolecularPathologyFindings(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                String.format("Operation %s failed due to a foreign key constraint violation for episode with ID: %s",
                    getCurrentMethodName(), episodeId));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @Operation(
        operationId = "getMolecularPathologyFinding",
        summary = "get molecular pathology finding for a specific molecularPathologyFindingId.",
        tags = {"molecular_pathology_finding"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = MolecularPathologyFindingDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode not found or is not the same as the saved in the database)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Molecular pathology finding not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/molecularpathologyfindings/{molecularPathologyFindingId}",
        produces = {"application/json"}
    )
    public ResponseEntity<MolecularPathologyFindingDto> getMolecularPathologyFinding(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("molecularPathologyFindingId") UUID molecularPathologyFindingId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        auditTrailService.addEntry(jwtClaimMap,
            String.format("Method %s invoked to retrieve molecular pathology finding with ID: %s", getCurrentMethodName(),
                molecularPathologyFindingId));

        if (!molecularPathologyFindingService.isMolecularPathologyFindingExist(molecularPathologyFindingId)) {
            auditTrailService.addEntry(jwtClaimMap,
                String.format("Operation %s failed: Molecular pathology finding with ID: %s not found", getCurrentMethodName(),
                    molecularPathologyFindingId));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        MolecularPathologyFindingDto savedMolecularPathologyFinding =
            molecularPathologyFindingService.getMolecularPathologyFinding(molecularPathologyFindingId);

        if (!episodeId.equals(savedMolecularPathologyFinding.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap,
                String.format(
                    "Operation %s failed: The episode ID in the path (%s) does not match the episode ID associated with the molecular pathology finding (%s)",
                    getCurrentMethodName(), episodeId, savedMolecularPathologyFinding.getEpisodeId()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(molecularPathologyFindingService.getMolecularPathologyFinding(molecularPathologyFindingId));
    }

    @Operation(
        operationId = "updateMolecularPathologyFinding",
        summary = "Change molecular pathology finding for an episode.",
        description = "Change molecular pathology finding for an episode.",
        tags = {"molecular_pathology_finding"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = MolecularPathologyFindingDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Check EpisodeId and Specimen)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Molecular pathology finding not found"),
            @ApiResponse(responseCode = "405", description = "Invalid Input")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/episodes/{episodeId}/molecularpathologyfindings/{molecularPathologyFindingId}",
        produces = {"application/json"},
        consumes = {"application/json"}
    )
    public ResponseEntity<MolecularPathologyFindingDto> updateMolecularPathologyFinding(@PathVariable("episodeId") UUID episodeId,
        UUID molecularPathologyFindingId, @Valid @RequestBody MolecularPathologyFindingDto molecularPathologyFindingDto) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        auditTrailService.addEntry(jwtClaimMap,
            String.format("Method %s invoked to update molecular pathology finding with ID: %s", getCurrentMethodName(),
                molecularPathologyFindingId));

        if (!molecularPathologyFindingService.isMolecularPathologyFindingExist(molecularPathologyFindingId)) {
            auditTrailService.addEntry(jwtClaimMap,
                String.format("Operation %s failed: Molecular pathology finding with ID: %s not found", getCurrentMethodName(),
                    molecularPathologyFindingId));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        MolecularPathologyFindingDto savedMolecularPathologyFindingDto =
            molecularPathologyFindingService.getMolecularPathologyFinding(molecularPathologyFindingId);

        if (!molecularPathologyFindingDto.getEpisodeId().equals(savedMolecularPathologyFindingDto.getEpisodeId()) || !episodeId.equals(
            molecularPathologyFindingDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, String.format(
                "Operation %s failed: The episode ID in the request body (%s) does not match the episode ID in the database (%s)",
                getCurrentMethodName(), molecularPathologyFindingDto.getEpisodeId(), savedMolecularPathologyFindingDto.getEpisodeId()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(molecularPathologyFindingService.updateMolecularPathologyFinding(molecularPathologyFindingId,
                molecularPathologyFindingDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                String.format("Operation %s failed due to a foreign key constraint violation. Verify episodeId.", getCurrentMethodName()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @Operation(
        operationId = "deleteMolecularPathologyFinding",
        summary = "Delete molecular pathology finding for an episode.",
        description = "Delete molecular pathology finding for an episode.",
        tags = {"molecular_pathology_finding"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Molecular pathology finding not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/episodes/{episodeId}/molecularpathologyfindings/{molecularPathologyFindingId}"
    )
    public ResponseEntity<Void> deleteMolecularPathologyFinding(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("molecularPathologyFindingId") UUID molecularPathologyFindingId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        auditTrailService.addEntry(jwtClaimMap,
            String.format("Method %s invoked to delete molecular pathology finding with ID: %s", getCurrentMethodName(),
                molecularPathologyFindingId));

        if (!molecularPathologyFindingService.isMolecularPathologyFindingExist(molecularPathologyFindingId)) {
            auditTrailService.addEntry(jwtClaimMap,
                String.format("Operation %s failed: Molecular pathology finding with ID: %s not found", getCurrentMethodName(),
                    molecularPathologyFindingId));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            molecularPathologyFindingService.deleteMolecularPathologyFinding(molecularPathologyFindingId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap,
                String.format("Operation %s failed due to an exception: %s", getCurrentMethodName(), exception.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }
}
