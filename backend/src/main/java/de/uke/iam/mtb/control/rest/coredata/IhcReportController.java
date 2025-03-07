package de.uke.iam.mtb.control.rest.coredata;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import de.uke.iam.mtb.api.model.IhcReportDto;
import de.uke.iam.mtb.api.server.IhcReportApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.EpisodeService;
import de.uke.iam.mtb.control.service.coredata.IhcReportService;
import de.uke.iam.mtb.control.service.coredata.SpecimenService;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

@RestController
@RequestMapping("/api")
@Secured({"ROLE_MTBDOCTOR"})
public class IhcReportController implements IhcReportApi {
    public AuditTrailService auditTrailService;
    public IhcReportService ihcReportService;
    public SpecimenService specimenService;
    public EpisodeService episodeService;

    public IhcReportController(IhcReportService ihcReportService,
            AuditTrailService auditTrailService, SpecimenService specimenService,
            EpisodeService episodeService) {
        this.ihcReportService = ihcReportService;
        this.auditTrailService = auditTrailService;
        this.specimenService = specimenService;
        this.episodeService = episodeService;
    }

    @Operation(operationId = "getAllIhcReports", tags = {"ihc_report"}, responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = IhcReportDto.class)))}),
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    @RequestMapping(method = RequestMethod.GET, value = "/episodes/{episodeId}/ihcReports",
            produces = {"application/json"})
    public ResponseEntity<List<IhcReportDto>> getAllIhcReports(
            @Parameter(name = "episodeId", description = "The id of the episode", required = true,
                    in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap(
                (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", get all ihcReports by episode with ID " + episodeId);

        // check for unknown episodeId
        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + " failed, episode with ID " + episodeId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            return ResponseEntity.ok(ihcReportService.getAllIhcReportDtos(episodeId));
        } catch (ForeignKeyException e) {
            auditTrailService.addEntry(jwtClaimMap,
                    getCurrentMethodName() + " failed, unhandled ForeignKeyException");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(operationId = "addIhcReport", tags = {"ihc_Report"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IhcReportDto.class))}),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized - The provided credentials are unknown"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")})
    @RequestMapping(method = RequestMethod.POST, value = "/episodes/{episodeId}/ihcReports",
            produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<IhcReportDto> addIhcReport(
            @Parameter(name = "episodeId", description = "The id of the episode", required = true,
                    in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId,
            @Parameter(name = "IhcReportDto", description = "add ihcReport to an episode.",
                    required = true) @Valid @RequestBody IhcReportDto ihcReportDto) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap(
                (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", add ihcReport to episode with ID " + episodeId);

        // check for episodeId mismatch
        if (!episodeId.equals(ihcReportDto.getEpisodeId())) {
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

        // check for assignment of id
        if (ihcReportDto.getId() != null) {
            ihcReportDto.setId(null);
        }

        // check for unknown specimenId
        if (!specimenService.isSpecimenExist(ihcReportDto.getSpecimenId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + " failed, specimen with ID " + ihcReportDto.getSpecimenId() + " does not exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(ihcReportService.addIhcReport(ihcReportDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                    getCurrentMethodName() + " failed, unhandled ForeignKeyException");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(operationId = "getIhcReport", tags = {"ihc_report"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IhcReportDto.class))}),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized - The provided credentials are unknown"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")})
    @RequestMapping(method = RequestMethod.GET,
            value = "/episodes/{episodeId}/ihcReports/{ihcReportId}",
            produces = {"application/json"})
    public ResponseEntity<IhcReportDto> getIhcReport(
            @Parameter(name = "episodeId", description = "The id of the episode", required = true,
                    in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId,
            @Parameter(name = "ihcReportId", description = "The id of the ihcReport",
                    required = true,
                    in = ParameterIn.PATH) @PathVariable("ihcReportId") UUID ihcReportId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap(
                (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + " get ihcReport by ID " + ihcReportId);

        // check for unknown episodeId
        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + " failed, episode with ID " + episodeId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // check for unknown ihcReportId
        if (!ihcReportService.isIhcReportExist(ihcReportId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + ", failed, ihcReport with ID " + ihcReportId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        IhcReportDto ihcReportDto = ihcReportService.getIhcReportDto(ihcReportId);

        // check for episodeId mismatch between path and database
        if (!episodeId.equals(ihcReportDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + " failed, Episode ID mismatch" + ihcReportDto.getEpisodeId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(ihcReportDto);
    }

    @Operation(operationId = "updateIhcReport", tags = {"ihc_report"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IhcReportDto.class))}),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized - The provided credentials are unknown"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")})
    @RequestMapping(method = RequestMethod.PUT,
            value = "/episodes/{episodeId}/ihcReports/{ihcReportId}",
            produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<IhcReportDto> updateIhcReport(
            @Parameter(name = "episodeId", description = "The id of the episode", required = true,
                    in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId,
            @Parameter(name = "ihcReportId", description = "The id of the ihcReport",
                    required = true,
                    in = ParameterIn.PATH) @PathVariable("ihcReportId") UUID ihcReportId,
            @Parameter(name = "IhcReportDto", description = "Change ihcReport for an episode.",
                    required = true) @Valid @RequestBody IhcReportDto ihcReportDto) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap(
                (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", update ihcReport by ID " + ihcReportId);

        // check for episodeId mismatch
        if (!episodeId.equals(ihcReportDto.getEpisodeId())) {
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

        // check for unknown specimenId
        if (!specimenService.isSpecimenExist(ihcReportDto.getSpecimenId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + " failed, specimen with ID " + ihcReportDto.getSpecimenId() + " does not exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        IhcReportDto savedIhcReportDto = ihcReportService.getIhcReportDto(ihcReportId);

        // check for episodeId mismatch between path and database 
        if (!episodeId.equals(savedIhcReportDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, Episode ID mismatch" + savedIhcReportDto.getEpisodeId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            return ResponseEntity.ok(ihcReportService.updateIhcReport(ihcReportDto));
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap,
                    getCurrentMethodName() + " failed, unhandled Exception");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(operationId = "deleteIhcReport", tags = {"ihcReport"},
            responses = {@ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized - The provided credentials are unknown"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
            })
    @RequestMapping(method = RequestMethod.DELETE,
            value = "/episodes/{episodeId}/ihcReports/{ihcReportId}")
    public ResponseEntity<Void> deleteIhcReport(
            @Parameter(name = "episodeId", description = "The id of the episode", required = true,
                    in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId,
            @Parameter(name = "ihcReportId", description = "The id of the ihcReport",
                    required = true,
                    in = ParameterIn.PATH) @PathVariable("ihcReportId") UUID ihcReportId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap(
                (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", delete ihcReport by ID " + ihcReportId);

        // check for unknown episodeId
        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + " failed, episode with ID " + episodeId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // check for unknown ihcReportId
        if (!ihcReportService.isIhcReportExist(ihcReportId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName()
                    + ", failed, ihcReport with ID " + ihcReportId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        IhcReportDto savedIhcReportDto = ihcReportService.getIhcReportDto(ihcReportId);

        // check for episodeId mismatch between path and database 
        if (!episodeId.equals(savedIhcReportDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, Episode ID mismatch" + savedIhcReportDto.getEpisodeId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            ihcReportService.deleteIhcReport(ihcReportId);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap,
                    getCurrentMethodName() + " failed, unhandled Exception");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
