package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.NgsReportDto;
import de.uke.iam.mtb.api.server.NgsReportApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.NgsReportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Secured({"ROLE_MTBDOCTOR"})
public class NgsReportController implements NgsReportApi {

    public NgsReportService ngsReportService;
    public AuditTrailService auditTrailService;

    public NgsReportController(NgsReportService ngsReportService, AuditTrailService auditTrailService) {
        this.ngsReportService = ngsReportService;
        this.auditTrailService = auditTrailService;
    }

    public ResponseEntity<NgsReportDto> addNgsReport(@PathVariable("episodeId") UUID episodeId, @RequestBody NgsReportDto ngsReportDto
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + "add to episode with ID " + episodeId);

        if (!episodeId.equals(ngsReportDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(ngsReportService.addNgsReport(ngsReportDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<List<NgsReportDto>> getAllNgsReports(
        @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        try {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get all by episode with ID " + episodeId);
            return ResponseEntity.ok(ngsReportService.getAllNgsReports(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    public ResponseEntity<NgsReportDto> getNgsReport(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("ngsReportId") String ngsReportId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by ID " + ngsReportId);

        if (!ngsReportService.isNgsReportExist(ngsReportId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, NgsReport with ID " + ngsReportId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        NgsReportDto ngsReportDto = ngsReportService.getNgsReport(ngsReportId);

        if (!episodeId.equals(ngsReportDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(ngsReportService.getNgsReport(ngsReportId));
    }

    public ResponseEntity<NgsReportDto> updateNgsReport(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("ngsReportId") String ngsReportId,
        @RequestBody NgsReportDto ngsReportDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by ID " + ngsReportId);

        if (!ngsReportService.isNgsReportExist(ngsReportId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, NgsReport with ID " + ngsReportId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        NgsReportDto savedNgsReportDto = ngsReportService.getNgsReport(ngsReportId);

        if (!ngsReportDto.getEpisodeId().equals(savedNgsReportDto.getEpisodeId()) || !episodeId.equals(ngsReportDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed unequal episode Ids for episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(ngsReportService.updateNgsReport(ngsReportId, ngsReportDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<Void> deleteNgsReport(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("ngsReportId") String ngsReportId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, "delete NgsReport by ID " + ngsReportId);

        if (!ngsReportService.isNgsReportExist(ngsReportId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, NgsReport with ID " + ngsReportId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            ngsReportService.deleteNgsReport(ngsReportId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed to delete NgsReport by ID " + ngsReportId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
