package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.NgsReportDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.coredata.NgsReport;
import de.uke.iam.mtb.control.models.mapper.coredata.NgsReportMapper;
import de.uke.iam.mtb.control.repository.coredata.NgsReportRepository;
import de.uke.iam.mtb.control.repository.coredata.TherapyRecommendationRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class NgsReportService {

    private NgsReportRepository ngsReportRepository;
    private NgsReportMapper ngsReportMapper;
    private EpisodeService episodeService;

    private TherapyRecommendationRepository therapyRecommendationRepository;

    public NgsReportService(NgsReportRepository ngsReportRepository, NgsReportMapper ngsReportMapper, TherapyRecommendationRepository therapyRecommendationRepository,
                            EpisodeService episodeService) {

        this.ngsReportRepository = ngsReportRepository;
        this.ngsReportMapper = ngsReportMapper;
        this.episodeService = episodeService;

        this.therapyRecommendationRepository = therapyRecommendationRepository;
    }

    public NgsReportDto addNgsReport(NgsReportDto ngsReportDto) throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(ngsReportDto.getEpisodeId())) {
            throw new ForeignKeyException("Episode with ID " + ngsReportDto.getEpisodeId() + " does not exist");
        }

        NgsReport ngsReport = ngsReportMapper.toEntity(ngsReportDto);

        Episode episodeReference = episodeService.getEpisodeReference(ngsReportDto.getEpisodeId());
        ngsReport.setEpisode(episodeReference);

        NgsReport savedNgsReport = ngsReportRepository.save(ngsReport);

        return ngsReportMapper.toDto(savedNgsReport);
    }

    public boolean isNgsReportExist(String ngsReportId) {
        return ngsReportRepository.existsById(ngsReportId);
    }

    public boolean isSpecimenAlreadyInNGSReport(UUID specimenId) {
        return ngsReportRepository.isSpecimenAlreadyInNGSReport(specimenId);
    }

    public NgsReportDto getNgsReport(String ngsReportId) {
        NgsReport savedNgsReport = ngsReportRepository.findById(ngsReportId).orElse(null);
        return ngsReportMapper.toDto(savedNgsReport);
    }

    public NgsReport getNgsReportReference(String ngsReportId) {

        return ngsReportRepository.getReferenceById(ngsReportId);
    }

    public List<NgsReportDto> getAllNgsReports(UUID episodeId) throws ForeignKeyException {

        if (!episodeService.isEpisodeExist(episodeId)) {
            throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
        }

        List<NgsReport> savedNgsReport = ngsReportRepository.getAllByEpisodeId(episodeId);

        return savedNgsReport.stream().map(ngsReportMapper::toDto).toList();
    }

    public NgsReportDto updateNgsReport(String ngsReportId, NgsReportDto ngsReportDto)
            throws ForeignKeyException {

        NgsReport ngsReport = ngsReportMapper.toEntity(ngsReportDto);
        NgsReport updatedNgsReport = ngsReportRepository.save(ngsReport);

        return ngsReportMapper.toDto(updatedNgsReport);
    }

    public void deleteNgsReport(String ngsReportId) {

        if (therapyRecommendationRepository.existsByNgsReportId(ngsReportId)) {
            throw new IllegalStateException("The NGS-Report cannot be deleted as it is referenced in other tables.");
        }

        ngsReportRepository.deleteById(ngsReportId);

    }

}
