package de.uke.iam.mtb.control.scheduler;

import de.uke.iam.mtb.api.model.LabNumberDto;
import de.uke.iam.mtb.control.helper.FileHelper;
import de.uke.iam.mtb.control.service.LabNumberService;
import de.uke.iam.mtb.control.service.StorageService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@EnableScheduling
@EnableAsync
@Transactional
public class HandleFiles {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);
    private final Path MAIN_DIR;
    private final Path UNZIP_DIR;
    private final Path ARCHIVE_DIR;
    private final LabNumberService labNumberService;

    @Autowired
    public HandleFiles(@Value("${MTB_MAIN_DIR}") String mainDir,
        @Value("${MTB_UNZIP_DIR}") String unzipDir,
        @Value("${MTB_ARCHIVE_DIR}") String archiveDir, LabNumberService labNumberService) {
        this.MAIN_DIR = Path.of(mainDir);
        this.UNZIP_DIR = Path.of(unzipDir);
        this.ARCHIVE_DIR = Path.of(archiveDir);
        this.labNumberService = labNumberService;
    }

    @Scheduled(fixedRateString = "${BIOINFO_SCHEDULER_RATE_IN_SECONDS}", timeUnit = TimeUnit.SECONDS)
    public void grabOneFile() throws IOException, InterruptedException {
        List<LabNumberDto> labNumberList = labNumberService.getAllUnassingedLabNumbers();
        Set<String> fileList = listFilesUsingFileWalk(this.MAIN_DIR, 1);

        List<LabNumberDto> listOfPossibleZipFiles = new ArrayList<>();

        for (String fileName : fileList) {
            String labNumber = getLabNumberFromFileName(fileName);
            try {
                listOfPossibleZipFiles.add(getLabNumber(labNumberList, labNumber));
            } catch (NoSuchElementException e) {
                logger.error("LabNumber {} currently unknown. A file exists, but labnumber-patient-matching has not yet been performed.",
                    labNumber);
            }
        }

        if (!listOfPossibleZipFiles.isEmpty()) {
            for (LabNumberDto possibleZipFile : listOfPossibleZipFiles) {
                if (possibleZipFile != null && possibleZipFile.getId() != null) {
                    Path path = Path.of(String.valueOf(this.MAIN_DIR), possibleZipFile.getId() + ".zip");
                    if (Files.isReadable(path)) {
                        handleZipFile(path);
                    }
                }
            }
        }
    }

    private void handleZipFile(Path zipFile) throws IOException, InterruptedException {
        // Wait until the file has finished being written to
        while (!Files.isReadable(zipFile)) {
            Thread.sleep(1000);
        }

        if (!Files.exists(this.UNZIP_DIR)) {
            Files.createDirectories(this.UNZIP_DIR);
        }

        if (!Files.exists(this.ARCHIVE_DIR)) {
            Files.createDirectories(this.ARCHIVE_DIR);
        }

        if (!FileHelper.isValidZipFile(zipFile)) {
            return;
        }

        try {
            FileHelper.unzip(zipFile, this.UNZIP_DIR);
        } catch (IOException exception) {
            logger.debug(Arrays.toString(exception.getStackTrace()));
            logger.debug("Unzipping of {} failed", zipFile);
        }

        try {
            Files.move(zipFile, this.ARCHIVE_DIR.resolve(zipFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            logger.debug(Arrays.toString(exception.getStackTrace()));
            logger.debug("Moving file to: {} failed", this.ARCHIVE_DIR);
        }
    }

    private LabNumberDto getLabNumber(List<LabNumberDto> labNumberList, String labNumber) throws NoSuchElementException {
        Optional<LabNumberDto> optional = labNumberList.stream().filter(o -> o.getId().equals(labNumber)).findFirst();
        return optional.orElseThrow(() -> new NoSuchElementException("No such lab number: " + labNumber));
    }

    private Set<String> listFilesUsingFileWalk(Path dir, int depth) throws IOException {
        try (Stream<Path> stream = Files.walk(dir, depth)) {
            return stream.filter(file -> !Files.isDirectory(file)).map(Path::getFileName)
                .map(Path::toString).collect(Collectors.toSet());
        }
    }

    private String getLabNumberFromFileName(String fileName) {
        return fileName.replace(".zip", "").substring(fileName.indexOf("_") + 1);
    }

    public static boolean isFilePresent(String directoryPath, String fileName) {
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File file = new File(directory, fileName);
            return file.exists() && file.isFile();
        }
        return false;
    }
}
