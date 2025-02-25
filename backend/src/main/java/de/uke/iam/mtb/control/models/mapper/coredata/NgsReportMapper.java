package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.NgsReportDto;
import de.uke.iam.mtb.control.models.coredata.NgsReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NgsReportMapper {

    @Mapping(target = "episodeId", source = "episode.id")
    @Mapping(target = "specimen", source = "specimen.id")
    NgsReportDto toDto(NgsReport ngsReport);

    @Mapping(target = "episode.id", source = "episodeId")
    @Mapping(target = "specimen.id", source = "specimen")
    NgsReport toEntity(NgsReportDto ngsReportDto);

}
