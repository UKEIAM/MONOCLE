package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.GuidelineTherapyDto;
import de.uke.iam.mtb.api.server.GuidelineTherapyApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.GuidelineTherapyService;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Secured({"ROLE_MTBDOCTOR"})
@RequestMapping("/api")
public class GuidelineTherapyController implements GuidelineTherapyApi {

    public GuidelineTherapyService guidelineTherapyService;
    public AuditTrailService auditTrailService;

    public GuidelineTherapyController(GuidelineTherapyService guidelineTherapyService, AuditTrailService auditTrailService) {
        this.guidelineTherapyService = guidelineTherapyService;
        this.auditTrailService = auditTrailService;
    }

    @Operation(
        operationId = "addGuidelineTherapy",
        summary = "add guideline therapy for an episode.",
        description = "add guideline therapy for an episode.",
        tags = { "guideline_therapy" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = GuidelineTherapyDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Conflict - Last Guideline Therapy for the given Episode already exists")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/episodes/{episodeId}/guidelinetherapies",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    @Validated
    public ResponseEntity<GuidelineTherapyDto> addGuidelineTherapy(@PathVariable("episodeId") UUID episodeId,
        @Valid @RequestBody GuidelineTherapyDto guidelineTherapyDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", add guideline therapy to episode with ID " + episodeId);

        if (!guidelineTherapyService.isGuideLineTherapyValid(guidelineTherapyDto)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, guidelineTherapyDto is not valid");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!episodeId.equals(guidelineTherapyDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(guidelineTherapyService.addGuidelineTherapy(guidelineTherapyDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(
        operationId = "getAllGuidelineTherapies",
        summary = "List all available guideline therapies for an episode.",
        tags = { "guideline_therapy" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GuidelineTherapyDto.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode not found)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/guidelinetherapies",
        produces = { "application/json" }
    )
    public ResponseEntity<List<GuidelineTherapyDto>> getAllGuidelineTherapies(@PathVariable("episodeId") UUID episodeId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        try {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", get by episode with ID " + episodeId);
            return ResponseEntity.ok(guidelineTherapyService.getAllGuidelineTherapies(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @Operation(
        operationId = "getGuidelineTherapy",
        summary = "get guideline therapy for a specific guidelineTherapyId.",
        tags = { "guideline_therapy" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = GuidelineTherapyDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode not found or is not the same as the saved in the database)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Guideline therapy not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/guidelinetherapies/{guidelineTherapyId}",
        produces = { "application/json" }
    )
    public ResponseEntity<GuidelineTherapyDto> getGuidelineTherapy(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("guidelineTherapyId") UUID guidelineTherapyId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by ID " + guidelineTherapyId);

        if (!guidelineTherapyService.isGuidelineTherapyExist(guidelineTherapyId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, guidelineTherapyId not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        GuidelineTherapyDto savedGuidelineTherapy = guidelineTherapyService.getGuidelineTherapy(guidelineTherapyId);

        if (!episodeId.equals(savedGuidelineTherapy.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(guidelineTherapyService.getGuidelineTherapy(guidelineTherapyId));
    }

    @Operation(
        operationId = "updateGuidelineTherapy",
        summary = "Change guideline therapy for an episode.",
        description = "Change guideline therapy  for an episode.",
        tags = { "guideline_therapy" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = GuidelineTherapyDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Guideline Therapy not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/episodes/{episodeId}/guidelinetherapies/{guidelineTherapyId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    public ResponseEntity<GuidelineTherapyDto> updateGuidelineTherapy(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("guidelineTherapyId") UUID guidelineTherapyId, @RequestBody GuidelineTherapyDto guidelineTherapyDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by ID " + guidelineTherapyId);

        if (!guidelineTherapyService.isGuideLineTherapyValid(guidelineTherapyDto)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, guidelineTherapyDto is not valid");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!guidelineTherapyService.isGuidelineTherapyExist(guidelineTherapyId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, guidelineTherapyId not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }


        GuidelineTherapyDto savedGuidelineTherapyDto = guidelineTherapyService.getGuidelineTherapy(guidelineTherapyId);

        if (!guidelineTherapyDto.getEpisodeId().equals(savedGuidelineTherapyDto.getEpisodeId()) || !episodeId.equals(
            guidelineTherapyDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(guidelineTherapyService.updateGuidelineTherapy(guidelineTherapyId, guidelineTherapyDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(
        operationId = "deleteGuidelineTherapy",
        summary = "Delete guideline therapy for an episode.",
        description = "Delete guideline therapy for an episode.",
        tags = { "guideline_therapy" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Guideline therapy not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/episodes/{episodeId}/guidelinetherapies/{guidelineTherapyId}"
    )
    public ResponseEntity<Void> deleteGuidelineTherapy(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("guidelineTherapyId") UUID guidelineTherapyId
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " delete by ID " + guidelineTherapyId);
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " delete by ID " + guidelineTherapyId);

        if (!guidelineTherapyService.isGuidelineTherapyExist(guidelineTherapyId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, guidelineTherapyId not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            guidelineTherapyService.deleteGuidelineTherapy(guidelineTherapyId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed with exception");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
