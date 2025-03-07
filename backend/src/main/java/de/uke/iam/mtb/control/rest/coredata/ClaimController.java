package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.ClaimDto;
import de.uke.iam.mtb.api.server.ClaimApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.ClaimService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ClaimController implements ClaimApi {

    public ClaimService claimService;
    public AuditTrailService auditTrailService;

    public ClaimController(ClaimService claimService, AuditTrailService auditTrailService) {
        this.claimService = claimService;
        this.auditTrailService = auditTrailService;
    }

    public ResponseEntity<ClaimDto> addClaim(@PathVariable("episodeId") UUID episodeId, @RequestBody ClaimDto claimDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " with episode ID " + episodeId);

        if (!episodeId.equals(claimDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (claimDto.getId() != null) {
            claimDto.setId(null);
        }

        try {
            return ResponseEntity.ok(claimService.addClaim(claimDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<List<ClaimDto>> getAllClaims(
        @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        try {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by episode with ID " + episodeId);
            return ResponseEntity.ok(claimService.getAllClaims(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    public ResponseEntity<ClaimDto> getClaim(@PathVariable("episodeId") UUID episodeId, @PathVariable("claimId") UUID claimId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by ID " + claimId);

        if (!claimService.isClaimExist(claimId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, claim with ID " + claimId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ClaimDto claimDto = claimService.getClaim(claimId);

        if (!episodeId.equals(claimDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, episode ID of dto and from request are not equal");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(claimService.getClaim(claimId));
    }

    public ResponseEntity<ClaimDto> updateClaim(@PathVariable("episodeId") UUID episodeId, @PathVariable("claimId") UUID claimId,
        @RequestBody ClaimDto claimDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by ID " + claimId);

        if (!claimService.isClaimExist(claimId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, claim with ID " + claimId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ClaimDto savedClaimDto = claimService.getClaim(claimId);

        if (!claimDto.getEpisodeId().equals(savedClaimDto.getEpisodeId()) || !episodeId.equals(claimDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed unequal episode Ids for episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(claimService.updateClaim(claimId, claimDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    public ResponseEntity<Void> deleteClaim(@PathVariable("episodeId") UUID episodeId, @PathVariable("claimId") UUID claimId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, "delete Claim by ID " + claimId);

        if (!claimService.isClaimExist(claimId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, claim with ID " + claimId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            claimService.deleteClaim(claimId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalStateException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed to delete Claim by ID " + claimId + ". Object is referenced somewhere else");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed to delete CLaim by ID " + claimId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
