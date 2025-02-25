package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.EcogStatusDto;
import de.uke.iam.mtb.api.server.EcogStatusApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.EcogStatusService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Secured({"ROLE_MTBDOCTOR"})
public class EcogStatusController implements EcogStatusApi {

    public EcogStatusService ecogStatusService;
    public AuditTrailService auditTrailService;

    public EcogStatusController(EcogStatusService ecogStatusService, AuditTrailService auditTrailService) {
        this.ecogStatusService = ecogStatusService;
        this.auditTrailService = auditTrailService;
    }

    public ResponseEntity<EcogStatusDto> addEcogStatus(@PathVariable("episodeId") UUID episodeId, @RequestBody EcogStatusDto ecogStatusDto
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add to episode with ID " + episodeId);
        if (!episodeId.equals(ecogStatusDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(ecogStatusService.addEcogStatus(ecogStatusDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<List<EcogStatusDto>> getAllEcogStatus(
            @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        try {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get all by episode with ID " + episodeId);
            return ResponseEntity.ok(ecogStatusService.getAllEcogStatuss(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    public ResponseEntity<EcogStatusDto> getEcogStatus(@PathVariable("episodeId") UUID episodeId, @PathVariable("ecogStatusId") UUID ecogStatusId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by ID " + ecogStatusId);
        if (!ecogStatusService.isEcogStatusExist(ecogStatusId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, ecog status with ID " + ecogStatusId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        EcogStatusDto ecogStatusDto = ecogStatusService.getEcogStatus(ecogStatusId);

        if (!episodeId.equals(ecogStatusDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, episode ID of dto and from request are not equal");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(ecogStatusService.getEcogStatus(ecogStatusId));
    }

    public ResponseEntity<EcogStatusDto> updateEcogStatus(@PathVariable("episodeId") UUID episodeId, @PathVariable("ecogStatusId") UUID ecogStatusId,
                                                                                @RequestBody EcogStatusDto ecogStatusDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by ID " + ecogStatusId);

        if(!ecogStatusDto.getId().equals(ecogStatusId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, Resource IDs of dto and from request are not equal");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!ecogStatusService.isEcogStatusExist(ecogStatusId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, ecog status with ID " + ecogStatusId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        EcogStatusDto savedEcogStatusDto = ecogStatusService.getEcogStatus(ecogStatusId);

        if (!ecogStatusDto.getEpisodeId().equals(savedEcogStatusDto.getEpisodeId()) || !episodeId.equals(ecogStatusDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, episode ID of dto and from request are not equal");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(ecogStatusService.updateEcogStatus(ecogStatusId, ecogStatusDto));
    }

    public ResponseEntity<Void> deleteEcogStatus(@PathVariable("episodeId") UUID episodeId, @PathVariable("ecogStatusId") UUID ecogStatusId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " delete by ID " + ecogStatusId);

        if (!ecogStatusService.isEcogStatusExist(ecogStatusId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, ecog status with ID " + ecogStatusId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            ecogStatusService.deleteEcogStatus(ecogStatusId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed to delete ecog status by ID " + ecogStatusId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
