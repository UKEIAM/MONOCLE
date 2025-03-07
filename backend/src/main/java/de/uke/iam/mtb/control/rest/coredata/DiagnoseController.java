package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.DiagnoseDto;
import de.uke.iam.mtb.api.server.DiagnoseApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.EpisodeService;
import de.uke.iam.mtb.control.service.coredata.DiagnoseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/api")
public class DiagnoseController implements DiagnoseApi {

    private final EpisodeService episodeService;
    public DiagnoseService diagnoseService;
    public AuditTrailService auditTrailService;
    private static final Logger logger = LoggerFactory.getLogger(DiagnoseController.class);

    public DiagnoseController(DiagnoseService diagnoseService, AuditTrailService auditTrailService, EpisodeService episodeService) {
        this.diagnoseService = diagnoseService;
        this.auditTrailService = auditTrailService;
        this.episodeService = episodeService;
    }

    @Override
    @Secured({"ROLE_MTBDOCTOR"})
    @Operation(
        operationId = "addDiagnose",
        summary = "add diagnose for an episode.",
        description = "add diagnose for an episode.",
        tags = {"diagnose"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = DiagnoseDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request - Foreign key constraint violated (EpisodeId or one of histologyResultId not found) or invalid germline diagnose"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Episode not found"),

        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/episodes/{episodeId}/diagnoses",
        produces = {"application/json"},
        consumes = {"application/json"}
    )
    public ResponseEntity<DiagnoseDto> addDiagnose(@PathVariable("episodeId") UUID episodeId, @RequestBody DiagnoseDto diagnoseDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (!episodeId.equals(diagnoseDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!diagnoseService.isGermlineDiagnosisCorrect(diagnoseDto)) {
            logger.error("The germlineDiagnosis attribute of Diagnose with ID {} is not correct", diagnoseDto.getId());
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, Bad Request for episode " + diagnoseDto.getEpisodeId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(diagnoseDto);
        }

        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, Episode not found episode " + diagnoseDto.getEpisodeId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", add diagnose to episode with ID " + episodeId);
            return ResponseEntity.ok(diagnoseService.addDiagnose(diagnoseDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    @Secured({"ROLE_MTBDOCTOR"})
    @Operation(
        operationId = "getAllDiagnoses",
        summary = "List all available diagnoses for an episode.",
        tags = {"diagnose"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DiagnoseDto.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode is not the same as the saved in the database)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "presentation not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/diagnoses",
        produces = {"application/json"}
    )
    public ResponseEntity<List<DiagnoseDto>> getAllDiagnoses(
        @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        try {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", get all diagnoses by episode with ID " + episodeId);
            return ResponseEntity.ok(diagnoseService.getAllDiagnoses(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @Override
    @Secured({"ROLE_MTBDOCTOR"})
    @Operation(
        operationId = "getDiagnose",
        summary = "get Diagnose information for a specific presentationId.",
        tags = {"diagnose"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = DiagnoseDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode not found or is not the same as the saved in the database)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Diagnose not found"),
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/diagnoses/{diagnoseId}",
        produces = {"application/json"}
    )
    public ResponseEntity<DiagnoseDto> getDiagnose(@PathVariable("episodeId") UUID episodeId, @PathVariable("diagnoseId") UUID diagnoseId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", get Diagnose by ID " + diagnoseId);

        if (!diagnoseService.isDiagnoseExist(diagnoseId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, diagnose with ID " + diagnoseId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        DiagnoseDto diagnoseDto = diagnoseService.getDiagnose(diagnoseId);

        if (!episodeId.equals(diagnoseDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, episode ID of dto and from request are not equal");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
         return ResponseEntity.ok(diagnoseDto);
    }

    @Override
    @Secured({"ROLE_MTBDOCTOR"})
    @Operation(
        operationId = "updateDiagnose",
        summary = "Change diagnose for an episode.",
        description = "Change diagnose for an episode.",
        tags = {"diagnose"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = DiagnoseDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request - Foreign key constraint violated (Provided Episode does not match the one associated with the Diagnose) or invalid germline diagnose"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Diagnose not found"),
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/episodes/{episodeId}/diagnoses/{diagnoseId}",
        produces = {"application/json"},
        consumes = {"application/json"}
    )
    public ResponseEntity<DiagnoseDto> updateDiagnose(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("diagnoseId") UUID diagnoseId,
        @RequestBody DiagnoseDto diagnoseDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (!diagnoseService.isDiagnoseExist(diagnoseId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, diagnose with ID " + diagnoseId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        DiagnoseDto savedDiagnoseDto = diagnoseService.getDiagnose(diagnoseId);

        if (!diagnoseDto.getEpisodeId().equals(savedDiagnoseDto.getEpisodeId()) || !episodeId.equals(diagnoseDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed unequal episode Ids for episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!diagnoseService.isGermlineDiagnosisCorrect(diagnoseDto)) {
            logger.error("The germlineDiagnosis attribute of Diagnose with ID {} is not correct", diagnoseDto.getId());
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed germline diagnose is inccorct for episode ID " + episodeId);
            return ResponseEntity.badRequest().body(diagnoseDto);
        }

        try {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", update Diagnose by ID " + diagnoseId);
            return ResponseEntity.ok(diagnoseService.updateDiagnose(diagnoseId, diagnoseDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @Override
    @Secured({"ROLE_MTBDOCTOR"})
    @Operation(
        operationId = "deleteDiagnose",
        summary = "Delete diagnose for an episode.",
        description = "Delete diagnose for an episode.",
        tags = {"diagnose"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Diagnose not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - referenced somewhere else")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/episodes/{episodeId}/diagnoses/{diagnoseId}"
    )
    public ResponseEntity<Void> deleteDiagnose(@PathVariable("episodeId") UUID episodeId, @PathVariable("diagnoseId") UUID diagnoseId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (!diagnoseService.isDiagnoseExist(diagnoseId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed to delete Diagnose, diagnose ID does not exist " + diagnoseId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            diagnoseService.deleteDiagnose(diagnoseId);
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", delete Diagnose by ID " + diagnoseId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalStateException exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed to delete Diagnose by ID " + diagnoseId + ". Object is referenced somewhere else");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed to delete Diagnose by ID " + diagnoseId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
