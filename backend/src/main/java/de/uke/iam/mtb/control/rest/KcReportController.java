package de.uke.iam.mtb.control.rest;

import de.uke.iam.mtb.api.model.KcReportDto;
import de.uke.iam.mtb.api.server.KcReportApi;
import de.uke.iam.mtb.control.exception.FileDeletionException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.EpisodeService;
import de.uke.iam.mtb.control.service.KcReportService;
import de.uke.iam.mtb.control.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@Tag(name = "kcReport", description = "the kcReport API")
@Secured({"ROLE_MTBDOCTOR"})
public class KcReportController implements KcReportApi {

    private final AuditTrailService auditTrailService;
    private final KcReportService kcReportService;
    private final StorageService storageService;
    private final EpisodeService episodeService;
    @Value("${MTB_REPORTS_DIR}")
    private final String MTB_REPORTS_DIR;

    public KcReportController(AuditTrailService auditTrailService, KcReportService kcReportService, StorageService storageService,
        EpisodeService episodeService, @Value("${MTB_REPORTS_DIR}") String mtbReportsDir) {
        this.auditTrailService = auditTrailService;
        this.kcReportService = kcReportService;
        this.storageService = storageService;
        this.episodeService = episodeService;
        this.MTB_REPORTS_DIR = mtbReportsDir;
    }


    @Override
    @Operation(
        operationId = "uploadKcReport",
        summary = "Receive and save KC-Reports on the file system"
    )
    @PostMapping(
        value = "/episodes/{episodeId}/kcReports",
        consumes = {"multipart/form-data"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = KcReportDto.class)))
        }),
        @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
        @ApiResponse(responseCode = "404", description = "Episode not found"),
        @ApiResponse(responseCode = "409", description = "Conflict (KC-Report with the same name already exists)"),
        @ApiResponse(responseCode = "415", description = "Unsupported Media Type (Only Pdf files allowed)"),
    })
    public ResponseEntity<List<KcReportDto>> uploadKcReport(
        @Parameter(name = "episodeId", description = "The ID of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId,
        @Parameter(name = "file", description = "The KC-Report file") @RequestPart(value = "file", required = false) MultipartFile file) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        auditTrailService.addEntry(jwtClaimMap, methodName + ", episodeId=" + episodeId);

        // Check if the file is a PDF
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            // If not a PDF, throw 405 Method Not Allowed
            auditTrailService.addEntry(jwtClaimMap, methodName + " failed, file is not a PDF");
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        // Check if episode does not exist, return 404 Not Found
        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, methodName + " failed, Episode not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Check if a file with the same name was already uploaded, return 409 Conflict
        if (kcReportService.isKcReportExistsByFileName(file.getOriginalFilename())) {
            auditTrailService.addEntry(jwtClaimMap, methodName + " failed, KC-Report with the same name already exists");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // initiate the KC report DTO
        KcReportDto kcReportDto = new KcReportDto();
        kcReportDto.setEpisodeId(episodeId);
        kcReportDto.setFileName(file.getOriginalFilename());

        // add or update the KC report to the database
        KcReportDto savedKcReport = kcReportService.addKcReport(kcReportDto);

        // save the Kc report file
        Path destinationPath = Path.of(MTB_REPORTS_DIR, episodeId.toString());
        String fileName = savedKcReport.getId().toString() + storageService.getFileExtension(
            Objects.requireNonNull(file.getOriginalFilename()));
        storageService.saveFile(file, destinationPath, fileName);

        return ResponseEntity.ok(kcReportService.getKcReportsByEpisodeId(episodeId));

    }

    @Override
    @Operation(
        operationId = "getKcReportsForEpisode",
        summary = "Get all relevante information about KC-Reports."
    )
    @GetMapping(
        value = "/episodes/{episodeId}/kcReports",
        produces = {"application/json"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = KcReportDto.class)))
        }),
        @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Episode not found")
    })
    public ResponseEntity<List<KcReportDto>> getKcReportsForEpisode(
        @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        auditTrailService.addEntry(jwtClaimMap, methodName + ", episodeId=" + episodeId);

        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, methodName + " failed, Episode not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(kcReportService.getKcReportsByEpisodeId(episodeId));

    }

    @Override
    @Operation(
        operationId = "getKcReportFile",
        summary = "get KC-Report with reportId from the file system"
    )
    @GetMapping(
        value = "/episodes/{episodeId}/kcReports/{kcReportId}",
        produces = {"application/pdf"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = "application/pdf", schema = @Schema(implementation = Resource.class))
        }),
        @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
        @ApiResponse(responseCode = "404", description = "Episode or KcReport not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Resource> getKcReportFile(
        @Parameter(name = "episodeId", description = "The id of the episode", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId,
        @Parameter(name = "kcReportId", description = "The ID of the KC-Report", required = true, in = ParameterIn.PATH) @PathVariable("kcReportId") UUID kcReportId
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        auditTrailService.addEntry(jwtClaimMap, methodName + ", episodeId=" + episodeId + ", kcReportId=" + kcReportId);

        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, methodName + " failed, Episode not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!kcReportService.isKcReportExistById(kcReportId)) {
            auditTrailService.addEntry(jwtClaimMap, methodName + " failed, KcReport not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Retrieve the report details (including file name) from the database
        KcReportDto kcReport = kcReportService.getKcReport(kcReportId);
        // Set the extension of the file
        String fileName = kcReport.getId().toString() + storageService.getFileExtension(kcReport.getFileName());
        Path filePath = Path.of(MTB_REPORTS_DIR, episodeId.toString(), fileName);
        try {
            // Load the file as a resource
            Resource resource = new UrlResource(filePath.toUri());

            // Check if the file exists and is readable
            if (!resource.exists() || !resource.isReadable()) {
                auditTrailService.addEntry(jwtClaimMap, methodName + " failed, File not found or unreadable");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Return the file with the content type and attachment header
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + kcReport.getFileName() + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
                .body(resource);

        } catch (MalformedURLException e) {
            // Log and handle file retrieval failure
            auditTrailService.addEntry(jwtClaimMap, methodName + " failed, Malformed URL");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @Override
    @Operation(
        operationId = "deleteKcReport",
        summary = "delete KC-Report with reportId from the file system"
    )
    @DeleteMapping(
        value = "/episodes/{episodeId}/kcReports/{kcReportId}"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - The provided credentials are unknown"),
        @ApiResponse(responseCode = "404", description = "Episode or KcReport not found")
    })
    public ResponseEntity<Void> deleteKcReport(
        @Parameter(name = "episodeId", description = "", required = true, in = ParameterIn.PATH) @PathVariable("episodeId") UUID episodeId,
        @Parameter(name = "kcReportId", description = "The ID of the KC-Report", required = true, in = ParameterIn.PATH) @PathVariable("kcReportId") UUID kcReportId
    ) {

        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        auditTrailService.addEntry(jwtClaimMap, methodName + ", episodeId=" + episodeId + ", kcReportId=" + kcReportId);

        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, methodName + " failed, Episode not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!kcReportService.isKcReportExistById(kcReportId)) {
            auditTrailService.addEntry(jwtClaimMap, methodName + " failed, KcReport not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        KcReportDto savedKcReport = kcReportService.getKcReport(kcReportId);
        // the name of the file is the id of the report + the extension of the file
        String fileName = savedKcReport.getId().toString() + storageService.getFileExtension(savedKcReport.getFileName());
        try {
            kcReportService.deleteKcReport(kcReportId);
            storageService.deleteFileIfExists(Path.of(MTB_REPORTS_DIR, episodeId.toString()), fileName);
            return ResponseEntity.ok().build();
        } catch (FileDeletionException e) {
            auditTrailService.addEntry(jwtClaimMap, methodName + " failed, File deletion failed");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
