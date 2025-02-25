package de.uke.iam.mtb.control.models.mapper.coredata;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.uke.iam.mtb.api.model.IhcReportDto;
import de.uke.iam.mtb.control.models.coredata.IhcReport;

@Mapper(componentModel = "spring")
public interface IhcReportMapper {
    @Mapping(target = "episodeId", source = "episode.id")
    @Mapping(target = "specimenId", source = "specimen.id")
    @Mapping(target = "msimmrResults", source = "msiMmrResults")
    IhcReportDto toDto(IhcReport ihcReport);

    @Mapping(target = "episode.id", source = "episodeId")
    @Mapping(target = "specimen.id", source = "specimenId")
    @Mapping(target = "msiMmrResults", source = "msimmrResults")
    IhcReport toEntity(IhcReportDto ihcReportDto);
}
