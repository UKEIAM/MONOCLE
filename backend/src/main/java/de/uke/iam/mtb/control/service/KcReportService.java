package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.api.model.KcReportDto;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.KcReport;
import de.uke.iam.mtb.control.models.mapper.KcReportMapper;
import de.uke.iam.mtb.control.repository.KcReportRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KcReportService {

    private final KcReportRepository kcReportRepository;
    private final EpisodeService episodeService;
    private final KcReportMapper kcReportMapper;
    // Lombok will automatically generate a constructor from all final objects in the class

    public KcReportDto addKcReport(KcReportDto kcReportDto) {
        // Get the episode reference
        Episode episodeReference = episodeService.getEpisodeReference(kcReportDto.getEpisodeId());
        // Map the DTO to the entity
        KcReport kcReport = kcReportMapper.toEntity(kcReportDto);
        // Set the episode reference
        kcReport.setEpisode(episodeReference);
        // Save the report
        KcReport savedKcReport = kcReportRepository.save(kcReport);
        return kcReportMapper.toDto(savedKcReport);
    }

    public boolean isKcReportExistById(UUID id) {
        return kcReportRepository.existsById(id);
    }

    public boolean isKcReportExistsByFileName(String fileName) {
        return kcReportRepository.existsByFileName(fileName);
    }

    public KcReportDto getKcReport(UUID id) {
        return kcReportMapper.toDto(kcReportRepository.findById(id).orElse(null));
    }

    public KcReportDto getKcReportByFileName(String fileName) {
        return kcReportMapper.toDto(kcReportRepository.findByFileName(fileName));
    }

    public List<KcReportDto> getKcReportsByEpisodeId(UUID episodeId) {
        return kcReportMapper.kcReportsToKcReportsDto(kcReportRepository.findAllByEpisodeId(episodeId));
    }

    public void deleteKcReport(UUID id) {
        kcReportRepository.deleteById(id);
    }
}
