package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.MolecularTherapyDto;
import de.uke.iam.mtb.api.server.MolecularTherapyApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.MolecularTherapyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Secured({"ROLE_MTBDOCTOR"})
public class MolecularTherapyController implements MolecularTherapyApi {

  public MolecularTherapyService molecularTherapyService;
  public AuditTrailService auditTrailService;

  public MolecularTherapyController(MolecularTherapyService molecularTherapyService, AuditTrailService auditTrailService) {
    this.molecularTherapyService = molecularTherapyService;
    this.auditTrailService = auditTrailService;
  }

  @Operation(
      operationId = "addMolecularTherapy",
      summary = "add molecularTherapy for an episode.",
      description = "add molecularTherapy for an episode.",
      tags = { "molecular_therapy" },
      responses = {
          @ApiResponse(responseCode = "200", description = "OK", content = {
              @Content(mediaType = "application/json", schema = @Schema(implementation = MolecularTherapyDto.class))
          }),
          @ApiResponse(responseCode = "400", description = "Bad Request - Foreign key constraint violated or episodeId not equal"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
          @ApiResponse(responseCode = "403", description = "Forbidden")
      }
  )
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/episodes/{episodeId}/moleculartherapies",
      produces = { "application/json" },
      consumes = { "application/json" }
  )
  public ResponseEntity<MolecularTherapyDto> addMolecularTherapy(@PathVariable("episodeId") UUID episodeId,
      @RequestBody MolecularTherapyDto molecularTherapyDto
  ) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add to episode with ID " + episodeId);

    if (!episodeId.equals(molecularTherapyDto.getEpisodeId())) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, body and path episodeID variables do not match");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      if (molecularTherapyDto.getId() != null) {
        molecularTherapyDto.setId(null);
      }
      return ResponseEntity.ok(molecularTherapyService.addMolecularTherapy(molecularTherapyDto));
    } catch (ForeignKeyException exception) {
      auditTrailService.addEntry(jwtClaimMap,
          getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @Operation(
      operationId = "getAllMolecularTherapies",
      summary = "List all moleculartherapies for an episode.",
      tags = { "molecular_therapy" },
      responses = {
          @ApiResponse(responseCode = "200", description = "OK", content = {
              @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MolecularTherapyDto.class)))
          }),
          @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode not found)"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
          @ApiResponse(responseCode = "403", description = "Forbidden")
      }
  )
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/episodes/{episodeId}/moleculartherapies",
      produces = { "application/json" }
  )
  public ResponseEntity<List<MolecularTherapyDto>> getAllMolecularTherapies(@PathVariable("episodeId") UUID episodeId) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

    try {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + "get by episode with ID " + episodeId);
      return ResponseEntity.ok(molecularTherapyService.getAllMolecularTherapies(episodeId));
    } catch (ForeignKeyException exception) {
      auditTrailService.addEntry(jwtClaimMap,
        getCurrentMethodName() + ", fail with ForeignKeyException with episode ID " + episodeId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

  }

  @Operation(
      operationId = "getMolecularTherapy",
      summary = "get molecularTherapy for a specific molecularTherapyId.",
      tags = { "molecular_therapy" },
      responses = {
          @ApiResponse(responseCode = "200", description = "OK", content = {
              @Content(mediaType = "application/json", schema = @Schema(implementation = MolecularTherapyDto.class))
          }),
          @ApiResponse(responseCode = "400", description = "Foreign key constraint violated (Episode not found or is not the same as the saved in the database)"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
          @ApiResponse(responseCode = "403", description = "Forbidden"),
          @ApiResponse(responseCode = "404", description = "molecularTherapy not found")
      }
  )
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/episodes/{episodeId}/moleculartherapies/{molecularTherapyId}",
      produces = { "application/json" }
  )
  public ResponseEntity<MolecularTherapyDto> getMolecularTherapy(@PathVariable("episodeId") UUID episodeId,
      @PathVariable("molecularTherapyId") UUID molecularTherapyId) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get by ID " + molecularTherapyId);

    if (!molecularTherapyService.isMolecularTherapyExist(molecularTherapyId)) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, molecularTherapy with ID " + molecularTherapyId + " does not exist");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    MolecularTherapyDto savedMolecularTherapy = molecularTherapyService.getMolecularTherapy(molecularTherapyId);

    if (!episodeId.equals(savedMolecularTherapy.getEpisodeId())) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, episode ID of dto and from request are not equal");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    return ResponseEntity.ok(molecularTherapyService.getMolecularTherapy(molecularTherapyId));
  }

  @Operation(
      operationId = "updateMolecularTherapy",
      summary = "Change molecularTherapy for an episode.",
      description = "Change molecularTherapy for an episode.",
      tags = { "molecular_therapy" },
      responses = {
          @ApiResponse(responseCode = "200", description = "OK", content = {
              @Content(mediaType = "application/json", schema = @Schema(implementation = MolecularTherapyDto.class))
          }),
          @ApiResponse(responseCode = "400", description = "Foreign key constraint violated or episodeID not euqal"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
          @ApiResponse(responseCode = "403", description = "Forbidden"),
          @ApiResponse(responseCode = "404", description = "molecularTherapy not found")
      }
  )
  @RequestMapping(
      method = RequestMethod.PUT,
      value = "/episodes/{episodeId}/moleculartherapies/{molecularTherapyId}",
      produces = { "application/json" },
      consumes = { "application/json" }
  )
  public ResponseEntity<MolecularTherapyDto> updateMolecularTherapy(@PathVariable("episodeId") UUID episodeId,
      @PathVariable("molecularTherapyId") UUID molecularTherapyId, @RequestBody MolecularTherapyDto molecularTherapyDto
  ) {
    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by ID " + molecularTherapyId);

    if (!molecularTherapyService.isMolecularTherapyExist(molecularTherapyId)) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed, molecularTherapy with ID " + molecularTherapyId + " does not exist");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    MolecularTherapyDto savedMolecularTherapyDto = molecularTherapyService.getMolecularTherapy(molecularTherapyId);

    if (!molecularTherapyDto.getEpisodeId().equals(savedMolecularTherapyDto.getEpisodeId()) || !episodeId.equals(
        molecularTherapyDto.getEpisodeId())) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed unequal episode Ids for episode ID " + episodeId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      return ResponseEntity.ok(molecularTherapyService.updateMolecularTherapy(molecularTherapyId, molecularTherapyDto));
    } catch (ForeignKeyException exception) {
      auditTrailService.addEntry(jwtClaimMap,
          getCurrentMethodName() + ", failed with ForeignKeyException with episode ID " + episodeId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

  }

  @Operation(
      operationId = "deleteMolecularTherapy",
      summary = "Delete molecularTherapy for an episode.",
      description = "Delete molecularTherapy for an episode.",
      tags = { "molecular_therapy" },
      responses = {
          @ApiResponse(responseCode = "200", description = "OK"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
          @ApiResponse(responseCode = "403", description = "Forbidden"),
          @ApiResponse(responseCode = "404", description = "molecularTherapy not found"),
          @ApiResponse(responseCode = "409", description = "Conflict")
      }
  )
  @RequestMapping(
      method = RequestMethod.DELETE,
      value = "/episodes/{episodeId}/moleculartherapies/{molecularTherapyId}"
  )
  public ResponseEntity<Void> deleteMolecularTherapy(@PathVariable("episodeId") UUID episodeId,
      @PathVariable("molecularTherapyId") UUID molecularTherapyId
  ) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " delete by ID " + molecularTherapyId);

    if (!molecularTherapyService.isMolecularTherapyExist(molecularTherapyId)) {
      auditTrailService.addEntry(jwtClaimMap,
          getCurrentMethodName() + ", failed, molecularTherapy ID does not exist " + molecularTherapyId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    try {
      molecularTherapyService.deleteMolecularTherapy(molecularTherapyId);
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (IllegalStateException exception) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed to delete molecularTherapy by ID " + molecularTherapyId + ". Object is referenced somewhere else");
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    } catch (Exception exception) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + ", failed to delete molecularTherapy by ID " + molecularTherapyId);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

  }
}
