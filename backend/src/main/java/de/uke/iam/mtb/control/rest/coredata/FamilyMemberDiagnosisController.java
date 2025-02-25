package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.FamilyMemberDiagnosisDto;
import de.uke.iam.mtb.api.server.FamilyMemberDiagnosisApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.FamilyMemberDiagnosisService;
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
public class FamilyMemberDiagnosisController implements FamilyMemberDiagnosisApi {

    public FamilyMemberDiagnosisService familyMemberDiagnosisService;
    public AuditTrailService auditTrailService;

    public FamilyMemberDiagnosisController(FamilyMemberDiagnosisService familyMemberDiagnosisService, AuditTrailService auditTrailService) {
        this.familyMemberDiagnosisService = familyMemberDiagnosisService;
        this.auditTrailService = auditTrailService;
    }

    public ResponseEntity<FamilyMemberDiagnosisDto> addFamilyMemberDiagnosis(@PathVariable("episodeId") UUID episodeId,
        @RequestBody FamilyMemberDiagnosisDto familyMemberDiagnosisDto
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add to episode with ID " + episodeId);

        if (!episodeId.equals(familyMemberDiagnosisDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (familyMemberDiagnosisDto.getId() != null) {
            familyMemberDiagnosisDto.setId(null);
        }

        try {
            return ResponseEntity.ok(familyMemberDiagnosisService.addFamilyMemberDiagnosis(familyMemberDiagnosisDto));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<List<FamilyMemberDiagnosisDto>> getAllFamilyMemberDiagnosis(
        @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        try {
            auditTrailService.addEntry(jwtClaimMap, "get all Family Member Diagnosiss by episode with ID " + episodeId);
            return ResponseEntity.ok(familyMemberDiagnosisService.getAllFamilyMemberDiagnosis(episodeId));
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    public ResponseEntity<FamilyMemberDiagnosisDto> getFamilyMemberDiagnosis(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("familyMemberDiagnosisId") UUID familyMemberDiagnosisId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by ID " + familyMemberDiagnosisId);

        if (!familyMemberDiagnosisService.isFamilyMemberDiagnosisExist(familyMemberDiagnosisId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, FamilyMemberDiagnosis with ID " + familyMemberDiagnosisId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        FamilyMemberDiagnosisDto familyMemberDiagnosisDto = familyMemberDiagnosisService.getFamilyMemberDiagnosis(familyMemberDiagnosisId);

        if (!episodeId.equals(familyMemberDiagnosisDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, episode ID of dto and from request are not equal");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(familyMemberDiagnosisDto);
    }

    public ResponseEntity<FamilyMemberDiagnosisDto> updateFamilyMemberDiagnosis(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("familyMemberDiagnosisId") UUID familyMemberDiagnosisId,
        @RequestBody FamilyMemberDiagnosisDto familyMemberDiagnosisDto
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by ID " + familyMemberDiagnosisId);

        if (!familyMemberDiagnosisDto.getId().equals(familyMemberDiagnosisId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, Resource IDs of dto and from request are not equal");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!familyMemberDiagnosisService.isFamilyMemberDiagnosisExist(familyMemberDiagnosisId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, FamilyMemberDiagnosis with ID " + familyMemberDiagnosisId + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        FamilyMemberDiagnosisDto savedFamilyMemberDiagnosisDto = familyMemberDiagnosisService.getFamilyMemberDiagnosis(
            familyMemberDiagnosisId);

        if (!familyMemberDiagnosisDto.getEpisodeId().equals(savedFamilyMemberDiagnosisDto.getEpisodeId()) || !episodeId.equals(
            familyMemberDiagnosisDto.getEpisodeId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed unequal episode Ids for episode ID " + episodeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(
            familyMemberDiagnosisService.updateFamilyMemberDiagnosis(familyMemberDiagnosisId, familyMemberDiagnosisDto));

    }

    public ResponseEntity<Void> deleteFamilyMemberDiagnosis(@PathVariable("episodeId") UUID episodeId,
        @PathVariable("familyMemberDiagnosisId") UUID familyMemberDiagnosisId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, "delete Family Member Diagnosis by ID " + familyMemberDiagnosisId);

        if (!familyMemberDiagnosisService.isFamilyMemberDiagnosisExist(familyMemberDiagnosisId)) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed to delete FamilyMemberDiagnosis, ID " + familyMemberDiagnosisId + " does not exist " );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            familyMemberDiagnosisService.deleteFamilyMemberDiagnosis(familyMemberDiagnosisId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed to delete FamilyMemberDiagnosisId by ID " + familyMemberDiagnosisId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
