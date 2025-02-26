package de.uke.iam.mtb.control.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uke.iam.mtb.api.model.EpisodeDto;
import de.uke.iam.mtb.api.model.LabNumberDto;
import de.uke.iam.mtb.api.model.NgsReportDto;
import de.uke.iam.mtb.api.model.SpecimenDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.coredata.NgsReport;
import de.uke.iam.mtb.control.models.coredata.Specimen;
import de.uke.iam.mtb.control.models.coredata.TumorCellContent;
import de.uke.iam.mtb.control.models.coredata.ngsreport.CopyNumberVariant;
import de.uke.iam.mtb.control.models.coredata.ngsreport.Metadata;
import de.uke.iam.mtb.control.models.coredata.ngsreport.RnaFusion;
import de.uke.iam.mtb.control.models.coredata.ngsreport.RnaSequence;
import de.uke.iam.mtb.control.models.coredata.ngsreport.SimpleVariant;
import de.uke.iam.mtb.control.models.mapper.EpisodeMapper;
import de.uke.iam.mtb.control.models.mapper.coredata.NgsReportMapper;
import de.uke.iam.mtb.control.models.mapper.coredata.SpecimenMapper;
import de.uke.iam.mtb.control.service.EpisodeService;
import de.uke.iam.mtb.control.service.LabNumberService;
import de.uke.iam.mtb.control.service.coredata.NgsReportService;
import de.uke.iam.mtb.control.service.coredata.SpecimenService;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@EnableAsync
@Transactional
public class BioInfoParser {

    private NgsReportService ngsReportService;
    private LabNumberService labNumberService;
    private EpisodeService episodeService;
    private SpecimenService specimenService;

    private NgsReportMapper ngsReportMapper;
    private EpisodeMapper episodeMapper;
    private SpecimenMapper specimenMapper;

    private static final Logger logger = LoggerFactory.getLogger(BioInfoParser.class);
    private static Path unzip_dir;

    private List<String> deletableFiles = new ArrayList<>();

    private NgsReport ngsReport;
    private List<CopyNumberVariant> copyNumberVariants;
    private List<Metadata> metadata;
    private List<RnaFusion> rnaFusions;
    private List<SimpleVariant> simpleVariants;
    private List<RnaSequence> rnaSequences;
    private TumorCellContent tumorCellContents;

    private String allowedEnding = ".json";

    public BioInfoParser(NgsReportService ngsReportService, LabNumberService labNumberService,
        EpisodeService episodeService, SpecimenService specimenService,
        NgsReportMapper ngsReportMapper,
        EpisodeMapper episodeMapper, SpecimenMapper specimenMapper,
        @Value("${MTB_UNZIP_DIR}") String MTB_UNZIP_DIR) {
        this.ngsReportMapper = ngsReportMapper;
        this.specimenService = specimenService;
        this.ngsReportService = ngsReportService;
        this.episodeMapper = episodeMapper;
        this.specimenMapper = specimenMapper;
        this.labNumberService = labNumberService;
        this.episodeService = episodeService;
        unzip_dir = Paths.get(MTB_UNZIP_DIR);
    }

    @Scheduled(fixedRateString = "${BIOINFO_SCHEDULER_RATE_IN_SECONDS}", timeUnit = TimeUnit.SECONDS)
    public void startBioInfoJSON() throws IOException, ForeignKeyException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        for (String listOfFile : listFilesUsingFileWalk(unzip_dir, 1)) {
            // FIXME: Change to "get LabNumberList from DB" to iterate, not from FileName
            if (listOfFile.contains("NGS-Report_")) {

                String labNumberFromFileString = getLabNumberFromFileName(listOfFile);

                File ngsReportFile = getFileFromFilePath(unzip_dir, listOfFile);
                logger.debug("Reading NGS-Report from {}", ngsReportFile);

                ngsReport = objectMapper.readValue(ngsReportFile, NgsReport.class);

                // Set ids of objects tmb, msi, brcaness, hrd
                if (ngsReport.getTmb() != null && ngsReport.getTmb().getId() == null) {
                    ngsReport.getTmb().setId(UUID.randomUUID());
                }
                if (ngsReport.getBrcaness() != null &&ngsReport.getBrcaness().getId() == null) {
                    ngsReport.getBrcaness().setId(UUID.randomUUID());
                }
                if (ngsReport.getMsi() != null &&ngsReport.getMsi().getId() == null) {
                    ngsReport.getMsi().setId(UUID.randomUUID());
                }
                if (ngsReport.getHrdScore() != null &&ngsReport.getHrdScore().getId() == null) {
                    ngsReport.getHrdScore().setId(UUID.randomUUID());
                }

                deletableFiles.add(ngsReportFile.getAbsolutePath());

                // Start fullfil ngsReport by BioInfo Json
                ngsReport = setCNVsToNGSReport(ngsReport,
                    getFileFromFilePath(unzip_dir, "CNVs_" + labNumberFromFileString + allowedEnding));
                logger.debug("Add CNVs to NGS Report");

                ngsReport = setMetadataToNGSReport(ngsReport,
                    getFileFromFilePath(unzip_dir, "metadata_" + labNumberFromFileString + allowedEnding));
                logger.debug("Add metadata to NGS Report");

                ngsReport = setRNAFusionsToNGSReport(ngsReport,
                    getFileFromFilePath(unzip_dir, "RNAFusions_" + labNumberFromFileString + allowedEnding));
                logger.debug("Add RNAFusions to NGS Report");

                ngsReport = setSimpleVariantsToNGSReport(ngsReport,
                    getFileFromFilePath(unzip_dir, "simpleVariants_" + labNumberFromFileString + allowedEnding));
                logger.debug("Add simpleVariants to NGS Report");

                ngsReport = setRNASequencesToNGSReport(ngsReport,
                    getFileFromFilePath(unzip_dir, "RNASequence_" + labNumberFromFileString + allowedEnding));
                logger.debug("Add RNASequence to NGS Report");

                ngsReport = setTumorCellContentToNGSReport(ngsReport,
                    getFileFromFilePath(unzip_dir, "TumorCellContent_" + labNumberFromFileString + allowedEnding));
                logger.debug("Add TumorCellContent to NGS Report");

                // NGSReport ID, wont define by BioINfo
                ngsReport.setId(UUID.randomUUID().toString()); //FIXME: Need to be send from BioInfo

                // Check if patho has combined a E/N-number (specimen label) with a labnumber
                if (isLabnumberRegistered(labNumberFromFileString)) {
                    // Yes, Patho has set a E/N-Number and Labnumber combination (Specimen is already created!)

                    List<SpecimenDto> specimenDtoList = getSpecimenListFromDBByLabNumberFromFile(labNumberFromFileString);

                    // is True, only when a Specimen with a lable combination was submit by patho
                    SpecimenDto specimenDto = specimenDtoList.get(0); //FIXME: What if multiple Specimen has the same Label!?

                    //FIXME: What is when, a NGS-Report with this specimen already exsits?
                    if (!ngsReportService.isSpecimenAlreadyInNGSReport(specimenDto.getId())) {
                        Specimen specimen = specimenMapper.toEntity(specimenDto);
                        ngsReport.setSpecimen(specimen);

                        // Add Episode ID from Patient in NGSReport
                        EpisodeDto episodeDto = episodeService.getEpisode(specimen.getEpisode().getId());
                        Episode episode = episodeMapper.toEntity(episodeDto);
                        ngsReport.setEpisode(episode);

                        NgsReportDto ngsReportDto = ngsReportMapper.toDto(ngsReport);
                        NgsReportDto resultNGSReport = ngsReportService.addNgsReport(ngsReportDto);
                        logger.debug("Added NgsReport {}", resultNGSReport.getId());

                        if (resultNGSReport.getId() != null) {
                            cleanUpTemparyFilesAndAssignedLabNumber(labNumberFromFileString);
                            // DONE! Exit.
                        } else {
                            //FIXME! What if NGSReport wont save in DB??
                            //Optinal FIx: Set Labnumber Assinged Status?
                            logger.error("Error! NgsReport won't save. LabNumber: {}", labNumberFromFileString);
                        }
                    } else {
                        // FIX! Specimen is already in NGSReport
                        logger.error("Error! Specimen is already set in NgsReport. LabNumber: {}", labNumberFromFileString);
                    }
                }
            }
        }
    }

    private void cleanUpTemparyFilesAndAssignedLabNumber(String labNumberFromFileString) throws IOException {
        // Update given LabNumber by Filename in DB as "used" (Assigned)
        LabNumberDto labNumberDto = labNumberService.getLabNumber(labNumberFromFileString);
        labNumberDto.setAssigned(true);
        labNumberDto.setAssignedOn(Instant.now());
        labNumberService.updateLabNumber(labNumberDto);

        // Delete all given Files
        for (Iterator<String> iterator = deletableFiles.iterator(); iterator.hasNext(); ) {
            String deletableFile = iterator.next();

            Path filePath = Paths.get(deletableFile);
            if (Files.isReadable(filePath)) {
                Files.delete(filePath);
                logger.debug("File was deleted: {}", deletableFile);
            }
            iterator.remove();
        }
    }

    private NgsReport setCNVsToNGSReport(NgsReport ngsReport, File file) throws IOException {
        if (Files.isReadable(file.toPath())) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

            copyNumberVariants = objectMapper.readValue(file, new TypeReference<>() {
            });
            logger.debug("Open file {}", file.getAbsolutePath());

            ngsReport.setCopyNumberVariants(copyNumberVariants);
            deletableFiles.add(file.getAbsolutePath());
        }
        return ngsReport;
    }

    private NgsReport setMetadataToNGSReport(NgsReport ngsReport, File file) throws IOException {
        if (Files.isReadable(file.toPath())) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

            metadata = objectMapper.readValue(file, new TypeReference<>() {
            });
            logger.debug("Open file {}", file.getAbsolutePath());

            ngsReport.setMetadata(metadata);
            deletableFiles.add(file.getAbsolutePath());
        }
        return ngsReport;
    }

    private NgsReport setRNAFusionsToNGSReport(NgsReport ngsReport, File file) throws IOException {
        if (Files.isReadable(file.toPath())) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

            rnaFusions = objectMapper.readValue(file, new TypeReference<>() {
            });
            logger.debug("Open file {}", file.getAbsolutePath());

            ngsReport.setRnaFusions(rnaFusions);
            deletableFiles.add(file.getAbsolutePath());
        }
        return ngsReport;
    }

    private NgsReport setSimpleVariantsToNGSReport(NgsReport ngsReport, File file)
        throws IOException {
        if (Files.isReadable(file.toPath())) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

            simpleVariants = objectMapper.readValue(file, new TypeReference<>() {
            });
            logger.debug("Open file {}", file.getAbsolutePath());

            ngsReport.setSimpleVariants(simpleVariants);
            deletableFiles.add(file.getAbsolutePath());
        }
        return ngsReport;
    }

    private NgsReport setRNASequencesToNGSReport(NgsReport ngsReport, File file) throws IOException {
        if (Files.isReadable(file.toPath())) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

            rnaSequences = objectMapper.readValue(file, new TypeReference<>() {
            });
            logger.debug("Open file {}", file.getAbsolutePath());

            ngsReport.setRnaSeqs(rnaSequences);
            deletableFiles.add(file.getAbsolutePath());
        }
        return ngsReport;
    }

    private NgsReport setTumorCellContentToNGSReport(NgsReport ngsReport, File file)
        throws IOException {
        if (Files.isReadable(file.toPath())) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

            tumorCellContents = objectMapper.readValue(file, TumorCellContent.class);
            logger.debug("Open file {}", file.getAbsolutePath());

            ngsReport.setTumorCellContent(tumorCellContents);
            deletableFiles.add(file.getAbsolutePath());
        }
        return ngsReport;
    }

    private boolean isLabnumberRegistered(String labNumberFromFileString) {
        LabNumberDto labNumberDto = labNumberService.getLabNumber(labNumberFromFileString);

        return (labNumberDto != null && labNumberDto.getSpecimenLabelling() != "");
    }

    private List<SpecimenDto> getSpecimenListFromDBByLabNumberFromFile(String labNumberFromFileString)
        throws ForeignKeyException {
        String specimenLabelFromDB = getSpecimenLabelFromFileLabnumber(
            labNumberFromFileString); // Return Specimen Label from DB (after Patho has submit the Patient/Speciment/Labnumber combination, online)
        return specimenService.getAllByLabelling(specimenLabelFromDB); // Return all Specimans by SpecimenLabel from Labnumber (File Name)
    }

    private String getSpecimenLabelFromFileLabnumber(String labNumberFromFileString) {
        LabNumberDto labNumberDtoFromDB = labNumberService.getLabNumber(labNumberFromFileString);
        return (labNumberDtoFromDB != null) ? labNumberDtoFromDB.getSpecimenLabelling() : null;
    }

    private UUID getEpisodeIdFromFileLabnumber(String labNumberFromFileString) {
        LabNumberDto labNumberDtoFromDB = labNumberService.getLabNumber(labNumberFromFileString);
        return (labNumberDtoFromDB != null) ? labNumberDtoFromDB.getEpisodeId() : null;
    }

    private String getLabNumberFromFileName(String fileName) {
        return fileName.replace(allowedEnding, "").substring(fileName.indexOf("_") + 1);
    }

    private File getFileFromFilePath(Path filePath, String fileName) {
        // I stringify and de-stingify because of build a os specific path incl. filename string
        return new File(String.valueOf(Paths.get(String.valueOf(filePath), fileName)));
    }

    private Set<String> listFilesUsingFileWalk(Path dir, int depth) throws IOException {
        try (Stream<Path> stream = Files.walk(dir, depth)) {
            return stream.filter(file -> !Files.isDirectory(file)).map(Path::getFileName)
                .map(Path::toString).collect(Collectors.toSet());
        }
    }
}
