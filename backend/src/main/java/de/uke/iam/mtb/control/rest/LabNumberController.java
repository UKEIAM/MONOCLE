package de.uke.iam.mtb.control.rest;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.LabNumberDto;
import de.uke.iam.mtb.api.server.LabNumberApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.LabNumberService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Secured({"ROLE_MTBPATHOLOGIST"})
public class LabNumberController implements LabNumberApi {

    private final LabNumberService labNumberService;
    private final AuditTrailService auditTrailService;

    public LabNumberController(LabNumberService labNumberService,
        AuditTrailService auditTrailService) {
        this.labNumberService = labNumberService;
        this.auditTrailService = auditTrailService;
    }

    @Override
    public ResponseEntity<List<LabNumberDto>> getLabNumbers() {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap,getCurrentMethodName() + " get all LabNumber");
        return ResponseEntity.ok(labNumberService.getAllLabNumbers());
    }

    @Override
    public ResponseEntity<Void> addLabNumber(@Valid @RequestBody LabNumberDto labNumberDto) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        try {
            auditTrailService.addEntry(jwtClaimMap,getCurrentMethodName() + " add Patient LabNumber: " + labNumberDto.getId());
            if (labNumberService.isLabNumberExists(labNumberDto.getId())) {
                auditTrailService.addEntry(jwtClaimMap,getCurrentMethodName() + " failed, labNumber with ID " + labNumberDto.getId() + " and specimen labelling " + labNumberDto.getSpecimenLabelling() + " already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            labNumberService.addLabNumber(labNumberDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ForeignKeyException e) {
            auditTrailService.addEntry(jwtClaimMap,getCurrentMethodName() + " failed, LabNumber with ID: " + labNumberDto.getId() + " and specimen labelling " + labNumberDto.getSpecimenLabelling() + " failed with ForeignKeyException");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<LabNumberDto> getLabNumber(String labNumberId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get LabNumber with ID: " + labNumberId);
        LabNumberDto savedLabNumber = labNumberService.getLabNumber(labNumberId.toUpperCase().strip());
        if (savedLabNumber == null) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, no LabNumber found with ID: " + labNumberId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(savedLabNumber);
        }
    }

    @Override
    public ResponseEntity<Boolean> isLabNumberExists(String labNumber) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " check if LabNumber exists with ID: " + labNumber);
        return ResponseEntity.ok(labNumberService.isLabNumberExists(labNumber));
    }
}
