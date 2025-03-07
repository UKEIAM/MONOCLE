package de.uke.iam.mtb.control.rest.coredata;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.CarePlanDto;
import de.uke.iam.mtb.api.server.CarePlanApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.CarePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@RequestMapping("/api")
@Secured({"ROLE_MTBDOCTOR"})
public class CarePlanController implements CarePlanApi {

    private final CarePlanService carePlanService;
    private final AuditTrailService auditTrailService;

    @Operation(
        operationId = "addCarePlan",
        summary = "add care plan for an episode.",
        description = "add care plan for an episode.",
        tags = {"care_plan"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CarePlanDto.class))
            }),
            @ApiResponse(responseCode = "400", description =
                "Bad Request - Foreign key constraint violated (Check episodeId, diagnosis, recommendations,"
                    + " geneticCounsellingRequest, rebiopsyRequests and studyInclusionRequests in your request) OR The given episode Id in the "
                    + "body is not the same as in the path"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/episodes/{episodeId}/careplans",
        produces = {"application/json"},
        consumes = {"application/json"}
    )
    public ResponseEntity<CarePlanDto> addCarePlan(
        @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId,
        @Parameter(name = "CarePlanDto", description = "add care plan for an episode.", required = true) @Valid @RequestBody CarePlanDto carePlanDto
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        auditTrailService.addEntry(jwtClaimMap,
            String.format("Method %s invoked to add a care plan to episode with ID: %s", getCurrentMethodName(), episodeId));

        if (!episodeId.equals(carePlanDto.getEpisodeId())) {

            auditTrailService.addEntry(jwtClaimMap,
                "Validation Error: The episode ID in the request body does not match the episode ID in the path.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (carePlanDto.getId() != null) {
            carePlanDto.setId(null);
        }

        try {
            return ResponseEntity.ok(carePlanService.addCarePlan(carePlanDto));
        } catch (ForeignKeyException exception) {

            auditTrailService.addEntry(jwtClaimMap, "Data Integrity Violation: Foreign key constraint error occurred. Verify episodeId, "
                + "diagnosis, recommendations, geneticCounsellingRequest, rebiopsyRequests, and studyInclusionRequests in the request.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(
        operationId = "getAllCarePlans",
        summary = "List all care plans for an episode.",
        tags = {"care_plan"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CarePlanDto.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request - Foreign key constraint violated (Episode not found)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/careplans",
        produces = {"application/json"}
    )
    public ResponseEntity<List<CarePlanDto>> getAllCarePlans(
        @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        try {

            auditTrailService.addEntry(jwtClaimMap, String.format("Method %s invoked to retrieve all care plans for episode with ID: %s",
                getCurrentMethodName(), episodeId));

            return ResponseEntity.ok(carePlanService.getAllCarePlans(episodeId));
        } catch (ForeignKeyException exception) {

            auditTrailService.addEntry(jwtClaimMap,
                String.format("Operation %s failed due to a foreign key constraint violation for episode"
                    + " with ID: %s", getCurrentMethodName(), episodeId));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @Operation(
        operationId = "getCarePlan",
        summary = "get care plan for a specific carePlanId.",
        tags = {"care_plan"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CarePlanDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request - Foreign key constraint violated (Episode not found or is not the same as the saved in the database)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found - care plan not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/episodes/{episodeId}/careplans/{carePlanId}",
        produces = {"application/json"}
    )
    public ResponseEntity<CarePlanDto> getCarePlan(
        @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId,
        @Parameter(name = "carePlanId", description = "The id of the care plan", required = true, in = ParameterIn.PATH) @PathVariable("carePlanId") UUID carePlanId
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        auditTrailService.addEntry(jwtClaimMap,
            String.format("Method %s invoked to retrieve care plan with ID: %s", getCurrentMethodName(), carePlanId));

        if (!carePlanService.isCarePlanExist(carePlanId)) {

            auditTrailService.addEntry(jwtClaimMap,
                String.format("Operation %s failed: Care plan with ID: %s not found", getCurrentMethodName(), carePlanId));

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        CarePlanDto carePlanDto = carePlanService.getCarePlan(carePlanId);

        if (!episodeId.equals(carePlanDto.getEpisodeId())) {

            auditTrailService.addEntry(jwtClaimMap,
                String.format("Operation %s failed: The episode ID in the path (%s) does not match the episode"
                    + " ID associated with the care plan (%s)", getCurrentMethodName(), episodeId, carePlanDto.getEpisodeId()));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(carePlanService.getCarePlan(carePlanId));
    }

    @Operation(
        operationId = "updateCarePlan",
        summary = "Change care plan for an episode.",
        description = "Change care plan for an episode.",
        tags = {"care_plan"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CarePlanDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request - Foreign key constraint violated (Check episodeId, diagnosis, recommendations, geneticCounsellingRequest, rebiopsyRequests and studyInclusionRequests in your request)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found - Care plan not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/episodes/{episodeId}/careplans/{carePlanId}",
        produces = {"application/json"},
        consumes = {"application/json"}
    )
    public ResponseEntity<CarePlanDto> updateCarePlan(
        @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId,
        @Parameter(name = "carePlanId", description = "The id of the care plan", required = true, in = ParameterIn.PATH) @PathVariable("carePlanId") UUID carePlanId,
        @Parameter(name = "CarePlanDto", description = "Change care plan for an episode.", required = true) @Valid @RequestBody CarePlanDto carePlanDto
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap,
            String.format("Method %s invoked to update care plan with ID: %s", getCurrentMethodName(), carePlanId));

        if (!carePlanService.isCarePlanExist(carePlanId)) {
            auditTrailService.addEntry(jwtClaimMap,
                String.format("Operation %s failed: Care plan with ID: %s not found", getCurrentMethodName(), carePlanId));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        CarePlanDto savedCarePlanDto = carePlanService.getCarePlan(carePlanId);

        if (!carePlanDto.getEpisodeId().equals(savedCarePlanDto.getEpisodeId()) || !episodeId.equals(carePlanDto.getEpisodeId())) {

            auditTrailService.addEntry(jwtClaimMap,
                String.format("Operation %s failed: The episode ID in the request body (%s) does not match "
                        + "the episode ID in the database (%s)", getCurrentMethodName(), carePlanDto.getEpisodeId(),
                    savedCarePlanDto.getEpisodeId()));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            return ResponseEntity.ok(carePlanService.updateCarePlan(carePlanId, carePlanDto));
        } catch (ForeignKeyException exception) {

            auditTrailService.addEntry(jwtClaimMap, String.format("Operation %s failed due to a foreign key constraint violation. Verify"
                + " episodeId, diagnosis, recommendations, geneticCounsellingRequest, rebiopsyRequests, and studyInclusionRequests in your"
                + " request.", getCurrentMethodName()));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @Operation(
        operationId = "deleteCarePlan",
        summary = "Delete care plan for an episode.",
        description = "Delete care plan for an episode.",
        tags = {"care_plan"},
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found - Care plan not found")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/episodes/{episodeId}/careplans/{carePlanId}"
    )
    public ResponseEntity<Void> deleteCarePlan(
        @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId,
        @Parameter(name = "carePlanId", description = "The id of care plan", required = true, in = ParameterIn.PATH) @PathVariable("carePlanId") UUID carePlanId
    ) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " by ID " + carePlanId);
        if (!carePlanService.isCarePlanExist(carePlanId)) {
            auditTrailService.addEntry(jwtClaimMap, "Failed to " + getCurrentMethodName() + " because care plan not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            carePlanService.deleteCarePlan(carePlanId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception exception) {
            auditTrailService.addEntry(jwtClaimMap, "Failed to " + getCurrentMethodName() + " because of unknown error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
