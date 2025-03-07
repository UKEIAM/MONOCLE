package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.DiagnoseDto;
import de.uke.iam.mtb.control.models.coredata.Diagnose;
import de.uke.iam.mtb.control.models.coredata.HistologyReport;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DiagnoseMapper {

  @Named("HistologyReportsToUUIDs")
  static List<UUID> HistologyReportsToUUIDs(List<HistologyReport> histologyReports) {
    return histologyReports.stream()
        .map(HistologyReport::getId)
        .collect(Collectors.toList());
  }

  @Named("UUIDsToHistologyReports")
  static List<HistologyReport> UUIDsToHistologyReports(List<UUID> histologyResults) {
    return histologyResults.stream()
        .map(id -> {
          HistologyReport histologyReport = new HistologyReport();
          histologyReport.setId(id);
          return histologyReport;
        })
        .collect(Collectors.toList());
  }

  @Mapping(target = "episodeId", source = "episode.id")
  @Mapping(target = "histologyResults", source = "histologyResults", qualifiedByName = "HistologyReportsToUUIDs")
  DiagnoseDto toDto(Diagnose diagnose);

  @Mapping(target = "episode.id", source = "episodeId")
  @Mapping(target = "histologyResults", source = "histologyResults", qualifiedByName = "UUIDsToHistologyReports")
  Diagnose toEntity(DiagnoseDto diagnoseDto);

}
