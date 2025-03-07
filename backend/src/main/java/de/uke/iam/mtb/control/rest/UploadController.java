package de.uke.iam.mtb.control.rest;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.server.UploadApi;
import de.uke.iam.mtb.control.exception.UploadFileContentTypeException;
import de.uke.iam.mtb.control.exception.UploadFileException;
import de.uke.iam.mtb.control.exception.UploadFileSizeException;
import de.uke.iam.mtb.control.scheduler.HandleFiles;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.StorageService;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class UploadController implements UploadApi {

    private final StorageService storageService;
    private final AuditTrailService auditTrailService;

    private final Path MAIN_DIR;
    private final Path UNZIP_DIR;
    private final Path ARCHIVE_DIR;

    public UploadController(StorageService storageService, AuditTrailService auditTrailService,
        @Value("${MTB_MAIN_DIR}") String mainDir,
        @Value("${MTB_UNZIP_DIR}") String unzipDir,
        @Value("${MTB_ARCHIVE_DIR}") String archiveDir) {
        this.storageService = storageService;
        this.auditTrailService = auditTrailService;
        this.MAIN_DIR = Path.of(mainDir);
        this.UNZIP_DIR = Path.of(unzipDir);
        this.ARCHIVE_DIR = Path.of(archiveDir);
    }

    @Override
    @Secured({"ROLE_MTBBIOINF"})
    public ResponseEntity<Void> uploadFile(@RequestParam("file") MultipartFile file) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap,
            getCurrentMethodName() + " upload file with name " + file.getName() + " and content type " + file.getContentType()
                + " and size of " + file.getSize());

        try {
            Path[] paths = {MAIN_DIR, UNZIP_DIR, ARCHIVE_DIR};

            for (Path path : paths) {
                if (HandleFiles.isFilePresent(path.toString(), file.getName())) {
                    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, file already exist");
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }

            storageService.store(file);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UploadFileContentTypeException exception) {
            // If file has a unallowed content type
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, not permitted content type");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UploadFileSizeException exception) {
            // If file has a unallowed file size
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, not permitted file size");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UploadFileException exception) {
            // All other possible IOExceptions
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, IOException");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
