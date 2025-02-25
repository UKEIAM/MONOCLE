package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.control.exception.FileDeletionException;
import de.uke.iam.mtb.control.exception.UploadFileContentTypeException;
import de.uke.iam.mtb.control.exception.UploadFileException;
import de.uke.iam.mtb.control.exception.UploadFileSizeException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

    private static Logger logger = LoggerFactory.getLogger(StorageService.class);
    private final Path rootLocation;
    @Value("${MTB_FILE_UPLOAD_MIN_BYTES}")
    String MTB_FILE_UPLOAD_MIN_BYTES;
    @Value("${MTB_FILE_ALLOWED_CONTENT_TYPES}")
    String MTB_FILE_ALLOWED_CONTENT_TYPES;

    @Autowired
    public StorageService(@Value("${MTB_MAIN_DIR}") String mainDir) {
        this.rootLocation = Paths.get(mainDir);
    }


    public void store(MultipartFile file) {
        if (file.getSize() <= Long.parseLong(MTB_FILE_UPLOAD_MIN_BYTES)) {
            throw new UploadFileSizeException(
                String.format("Unauthorised file size, only files larger than %s Bytes are allowed.", MTB_FILE_UPLOAD_MIN_BYTES));
        }

        if (!getListOfString(MTB_FILE_ALLOWED_CONTENT_TYPES).contains(file.getContentType())) {
            throw new UploadFileContentTypeException(
                String.format("Unauthorised content type, only %s files are allowed.", MTB_FILE_ALLOWED_CONTENT_TYPES));
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path destinationFile = this.rootLocation.resolve(Paths.get(file.getOriginalFilename()))
                .normalize().toAbsolutePath();
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UploadFileException("Failed to store file.", e);
        }
    }

    public String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex != fileName.length() - 1) { // Check if '.' exists and is not the last character
            return fileName.substring(dotIndex); // Include the '.' in the substring
        }
        return ""; // Return an empty string if there's no extension or '.' is the last character
    }

    public void saveFile(MultipartFile file, Path destinationPath, String fileNameWithExtension) {
        try {
            Path destinationFile = destinationPath.resolve(fileNameWithExtension).normalize().toAbsolutePath();

            // Create directories if they don’t exist
            Files.createDirectories(destinationPath);

            // Copy the file to the destination
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                logger.info("File saved to: {}", destinationFile);
            }
        } catch (IOException e) {
            throw new UploadFileException("Failed to store file.", e);
        }
    }

    public void deleteFileIfExists(Path destinationPath, String fileName) {
        Path fileToDelete = destinationPath.resolve(fileName).normalize().toAbsolutePath();

        try {
            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete);
            }
        } catch (IOException e) {
            throw new FileDeletionException("Failed to delete file.", e);
        }
    }

    // Helper method to parse comma-separated string of allowed content types into a List<String>
    private List<String> getListOfString(String commaSeparatedValues) {
        return Arrays.asList(commaSeparatedValues.split(","));
    }
}
