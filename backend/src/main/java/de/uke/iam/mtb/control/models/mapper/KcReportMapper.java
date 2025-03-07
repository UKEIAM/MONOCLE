package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.KcReportDto;
import de.uke.iam.mtb.control.models.KcReport;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KcReportMapper {

    @Mapping(target = "episodeId", source = "episode.id")
    KcReportDto toDto(KcReport kcReport);

    @Mapping(target = "episode.id", source = "episodeId")
    KcReport toEntity(KcReportDto kcReportDto);

    List<KcReportDto> kcReportsToKcReportsDto(List<KcReport> kcReports);

    List<KcReport> kcReportsDtoToKcReports(List<KcReportDto> kcReportsDto);
}
