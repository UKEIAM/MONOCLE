package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.SpecimenCollectionDto;
import de.uke.iam.mtb.api.model.SpecimenDto;
import de.uke.iam.mtb.api.server.SpecimenApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.DiagnoseService;
import de.uke.iam.mtb.control.service.coredata.SpecimenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.time.LocalDate;
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
@RequestMapping("/api")
@Secured({"ROLE_MTBDOCTOR", "ROLE_MTBPATHOLOGIST"})
public class SpecimenController implements SpecimenApi {

    private final DiagnoseService diagnoseService;
    private final SpecimenService specimenService;
    private final AuditTrailService auditTrailService;

    public SpecimenController(SpecimenService specimenService, AuditTrailService auditTrailService, DiagnoseService diagnoseService) {
        this.specimenService = specimenService;
        this.auditTrailService = auditTrailService;
        this.diagnoseService = diagnoseService;
    }

    @Operation(
        operationId = "addSpecimen",
        summary = "add specimen for an episode.",
        description = "add specimen for an episode.",
        tags = { "specimen" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = SpecimenDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (EpisodeId not found) or body and path EpisodeId variables do not match"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/episodes/{episodeId}/specimens",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    public ResponseEntity<SpecimenDto> addSpecimen(@PathVariable("episodeId") UUID episodeId, @RequestBody SpecimenDto specimenDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", add specimen to episode with ID " + episodeId);

        if (!episodeId.equals(specimenDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // check if ICD10 Code is the same in Diagnose
        try {
            if (diagnoseService.getAllDiagnoses(episodeId).stream().noneMatch(diagnoseDto -> diagnoseDto.getIcd10().getCode().equals(specimenDto.getIcd10().getCode()))) {
                auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, ICD10 Code does not exist in Diagnose for EpisodeId=" + episodeId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (ForeignKeyException e) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, Episode with ID " + episodeId + " does not exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (isCollectionInvalid(specimenDto.getCollection().getDate(), specimenDto.getCollection().getMethod(),
            specimenDto.getCollection().getLocalization())) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + " failed, 'date', 'method', and 'localization' must either all be set or all be null, episodeId= "
                    + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(specimenService.addSpecimen(specimenDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, episodeId does not exist, episodeId= " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(
        operationId = "getAllSpecimens",
        summary = "List all available specimens for an episode.",
        tags = { "specimen" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SpecimenDto.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode is not the same as the saved in the database)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/specimens",
        produces = { "application/json" }
    )
    public ResponseEntity<List<SpecimenDto>> getAllSpecimens(
        @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        try {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", get all Specimens by episode with ID " + episodeId);
            return ResponseEntity.ok(specimenService.getAllSpecimens(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, episodeId does not exist, episodeId= " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @Operation(
        operationId = "getSpecimen",
        summary = "get specimen information for a specific specimenId.",
        tags = { "specimen" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = SpecimenDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode not found or is not the same as the saved in the database)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Specimen not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/specimens/{specimenId}",
        produces = { "application/json" }
    )
    public ResponseEntity<SpecimenDto> getSpecimen(@PathVariable("episodeId") UUID episodeId, @PathVariable("specimenId") UUID specimenId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", get Specimen by ID " + specimenId);

        if (!specimenService.isSpecimenExist(specimenId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + " failed, specimenId does not exist, episodeId= " + episodeId + ", specimenId= " + specimenId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        SpecimenDto specimenDto = specimenService.getSpecimen(specimenId);

        if (!episodeId.equals(specimenDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(specimenService.getSpecimen(specimenId));
    }

    public ResponseEntity<SpecimenDto> updateSpecimen(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("specimenId") UUID specimenId,
        @RequestBody SpecimenDto specimenDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, "update Specimen by ID " + specimenId);

        if (!specimenService.isSpecimenExist(specimenId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + " failed, specimenId does not exist, episodeId= " + episodeId + ", specimenId= " + specimenId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // check if ICD10 Code is the same in Diagnose
        try {
            if (diagnoseService.getAllDiagnoses(episodeId).stream().noneMatch(diagnoseDto -> diagnoseDto.getIcd10().getCode().equals(specimenDto.getIcd10().getCode()))) {
                auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, ICD10 Code does not exist in Diagnose for EpisodeId=" + episodeId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (ForeignKeyException e) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, Episode with ID " + episodeId + " does not exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (isCollectionInvalid(specimenDto.getCollection().getDate(), specimenDto.getCollection().getMethod(),
            specimenDto.getCollection().getLocalization())) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + " failed, 'date', 'method', and 'localization' must either all be set or all be null, episodeId= "
                    + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        SpecimenDto savedSpecimenDto = specimenService.getSpecimen(specimenId);

        if (!specimenDto.getEpisodeId().equals(savedSpecimenDto.getEpisodeId()) || !episodeId.equals(specimenDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, episodeIds do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(specimenService.updateSpecimen(specimenId, specimenDto));
    }

    @Operation(
        operationId = "deleteSpecimen",
        summary = "Delete specimen for an episode.",
        description = "Delete specimen for an episode.",
        tags = { "specimen" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Specimen not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/episodes/{episodeId}/specimens/{specimenId}"
    )
    public ResponseEntity<Void> deleteSpecimen(@PathVariable("episodeId") UUID episodeId, @PathVariable("specimenId") UUID specimenId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " delete Specimen by ID " + specimenId);

        if (!specimenService.isSpecimenExist(specimenId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + " failed, specimenId does not exist, episodeId= " + episodeId + ", specimenId= " + specimenId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            specimenService.deleteSpecimen(specimenId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalStateException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + " failed, specimenId is referenced somewhere else, specimenId= " + specimenId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private boolean isCollectionInvalid(LocalDate date, SpecimenCollectionDto.MethodEnum method,
        SpecimenCollectionDto.LocalizationEnum localisation) {
        // They cannot be empty strings, because then the json converter will fail
        boolean isDateSet = date != null;
        boolean isMethodSet = method != null;
        boolean isLocalizationSet = localisation != null;

        // Check if any field is set but not all of them are set
        return (isDateSet || isMethodSet || isLocalizationSet) &&
            !(isDateSet && isMethodSet && isLocalizationSet);
    }
}
