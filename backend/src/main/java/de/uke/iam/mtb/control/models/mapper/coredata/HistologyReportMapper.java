package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.HistologyReportDto;
import de.uke.iam.mtb.control.models.coredata.HistologyReport;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistologyReportMapper {
  @Mapping(target = "episodeId", source = "episode.id")
  @Mapping(target = "specimen", source = "specimen.id")
  @Mapping(target = "differentiationDegree", source = "differentiationDegree")
  HistologyReportDto toDto(HistologyReport histologyReport);

  @Mapping(target = "episode.id", source = "episodeId")
  @Mapping(target = "specimen.id", source = "specimen")
  @Mapping(target = "differentiationDegree", source = "differentiationDegree")
  HistologyReport toEntity(HistologyReportDto histologyReportDto);
}
