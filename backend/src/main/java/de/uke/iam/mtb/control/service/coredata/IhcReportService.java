package de.uke.iam.mtb.control.service.coredata;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.uke.iam.mtb.api.model.IhcReportDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.coredata.IhcReport;
import de.uke.iam.mtb.control.models.mapper.coredata.IhcReportMapper;
import de.uke.iam.mtb.control.repository.coredata.IhcReportRepository;
import de.uke.iam.mtb.control.service.EpisodeService;

@Service
@Transactional
public class IhcReportService {
    private final IhcReportRepository ihcReportRepository;
    private final IhcReportMapper ihcReportMapper;

    private final EpisodeService episodeService;

    public IhcReportService(IhcReportRepository ihcReportRepository, IhcReportMapper ihcReportMapper,
            EpisodeService episodeService) {
        this.ihcReportRepository = ihcReportRepository;
        this.ihcReportMapper = ihcReportMapper;
        this.episodeService = episodeService;
    }

    public boolean isIhcReportExist(UUID ihcReportId) {
        return ihcReportRepository.existsById(ihcReportId);
    }

    public IhcReportDto addIhcReport(IhcReportDto ihcReportDto) throws ForeignKeyException {
        IhcReport ihcReport = ihcReportMapper.toEntity(ihcReportDto);
        for (var result : ihcReport.getProteinExpressionResults()) {
            if (result.getId() != null) continue;
            result.setId(UUID.randomUUID());
        }
        for (var result : ihcReport.getMsiMmrResults()) {
            if (result.getId() != null) continue;
            result.setId(UUID.randomUUID());
        }
        IhcReport savedIhcReport = ihcReportRepository.save(ihcReport);

        return ihcReportMapper.toDto(savedIhcReport);
    }

    public List<IhcReportDto> getAllIhcReportDtos(UUID episodeId) throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(episodeId)) {
            throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
        }

        return ihcReportRepository.getAllByEpisodeId(episodeId).stream().map(ihcReportMapper::toDto).toList();
    }

    public IhcReportDto getIhcReportDto(UUID ihcReportId) {
        return ihcReportMapper.toDto(ihcReportRepository.getReferenceById(ihcReportId));
    }

    public IhcReportDto updateIhcReport(IhcReportDto ihcReportDto) {
        IhcReport ihcReport = ihcReportMapper.toEntity(ihcReportDto);
        for (var result : ihcReport.getProteinExpressionResults()) {
            if (result.getId() != null) continue;
            result.setId(UUID.randomUUID());
        }
        for (var result : ihcReport.getMsiMmrResults()) {
            if (result.getId() != null) continue;
            result.setId(UUID.randomUUID());
        }
        IhcReport updatedIhcReport = ihcReportRepository.save(ihcReport);
        return ihcReportMapper.toDto(updatedIhcReport);
    }

    public void deleteIhcReport(UUID ihcReportId) {
        ihcReportRepository.deleteById(ihcReportId);
    }
}
