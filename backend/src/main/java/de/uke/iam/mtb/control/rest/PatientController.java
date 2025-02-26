package de.uke.iam.mtb.control.rest;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.PatientDto;
import de.uke.iam.mtb.api.server.PatientApi;
import de.uke.iam.mtb.control.exception.DuplicateSoarianIdException;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.PatientService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PatientController implements PatientApi {

    private final PatientService patientService;
    private final AuditTrailService auditTrailService;

    public PatientController(PatientService patientService, AuditTrailService auditTrailService) {
        this.patientService = patientService;
        this.auditTrailService = auditTrailService;
    }

    @Override
    @Secured({"ROLE_MTBDOCTOR", "ROLE_MTBPATHOLOGIST"})
    @Operation(
        operationId = "getPatients",
        summary = "List all available patients with the latest Episode.",
        tags = {"patient"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PatientDto.class)))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/patients",
        produces = {"application/json"})
    public ResponseEntity<List<PatientDto>> getPatients() {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName());
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @Operation(
        operationId = "addPatient",
        summary = "Add new patient to database.",
        description = "Add new patient to database.",
        tags = {"patient"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = PatientDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request - some errors in the sent data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Unique constraint violated")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/patients",
        produces = {"application/json"},
        consumes = {"application/json"})
    public ResponseEntity<PatientDto> addPatient(@Valid @RequestBody PatientDto patientDto) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // Gender and soarian ID are required in KDS
        if (patientDto.getGender() == null || patientDto.getSoarianId() == null) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, Bad Request gender and soarian ID are required for patient with soarian ID "
                    + patientDto.getSoarianId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Check the number of digits in municipalityKey
        if (patientDto.getMunicipalityKey().length() != 5) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, Bad Request municipality key requires 5 digits of patient with soarian ID "
                    + patientDto.getSoarianId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            PatientDto savedPatient = patientService.addPatient(patientDto);
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", savedPatientSoarianId=" + savedPatient.getSoarianId());
            return ResponseEntity.ok(savedPatient);
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (DuplicateSoarianIdException exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, conflict soarianId duplicated");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Override
    @Secured({"ROLE_MTBDOCTOR"})
    @Operation(
        operationId = "getPatient",
        summary = "Get one Patient with the latest Episode.",
        tags = {"patient"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = PatientDto.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/patients/{id}",
        produces = {"application/json"})
    public ResponseEntity<PatientDto> getPatient(UUID id) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", patientId:" + id);

        PatientDto patientById = patientService.getPatient(id);

        if (patientById != null) {
            return ResponseEntity.ok(patientById);
        } else {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed to get patient with patientId:" + id);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @Secured({"ROLE_MTBDOCTOR"})
    @Operation(
        operationId = "updatePatient",
        summary = "Update one patient.",
        tags = {"patient"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = PatientDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request - some errors in the sent data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "409", description = "Unique constraint violated")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/patients/{id}",
        produces = {"application/json"},
        consumes = {"application/json"})
    public ResponseEntity<PatientDto> updatePatient(UUID id, PatientDto patient) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (!id.equals(patient.getId())) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, patient ids not equal patientId:" + id + " vs. id in patient object:"
                    + patient.getId());
            return ResponseEntity.badRequest().build();
        }

        if (!patientService.isPatientExist(id)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, patient does not exist with patientId:" + id);
            return ResponseEntity.notFound().build();
        }

        // Gender and soarian ID are required in KDS
        if (patient.getGender() == null || patient.getSoarianId() == null) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, Bad Request gender and soarian ID are required for patient with soarian ID "
                    + patient.getSoarianId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Check the number of digits in municipalityKey
        if (patient.getMunicipalityKey().length() != 5) {
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + ", failed, Bad Request municipality key requires 5 digits of patient with soarian ID "
                    + patient.getSoarianId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", patientId:" + id);
            // If the patient doesn't exist in the database
            PatientDto savedPatient = patientService.updatePatient(id, patient);
            return ResponseEntity.ok(savedPatient);
        } catch (DuplicateSoarianIdException exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, patient already exists with id:" + id);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (ForeignKeyException exception) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed with ForeignKeyException patientId:" + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
