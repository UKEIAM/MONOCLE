package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.StudyInclusionRequestDto;
import de.uke.iam.mtb.control.models.coredata.NgsReport;
import de.uke.iam.mtb.control.models.coredata.StudyInclusionRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StudyInclusionRequestMapper {

  // TODO : check if the methods works as they should and if they are mandatory
  @Named("NgsReportsToStrings")
  static List<String> NgsReportsToStrings(List<NgsReport> ngsReports) {
    if (ngsReports == null) {
      return Collections.emptyList();
    }
    return ngsReports.stream()
        .map(NgsReport::getId)
        .collect(Collectors.toList());
  }

  @Named("StringsToNgsReports")
  static List<NgsReport> StringsToNgsReports(List<String> ngsReports) {
    if (ngsReports == null) {
      return Collections.emptyList();
    }
    return ngsReports.stream()
        .map(id -> {
          NgsReport ngsReport = new NgsReport();
          ngsReport.setId(id);
          return ngsReport;
        })
        .collect(Collectors.toList());
  }

    @Mapping(target = "episodeId", source = "episode.id")
    @Mapping(target = "reason", source = "diagnose.id")
    @Mapping(target = "ngsReports", source = "ngsReports", qualifiedByName = "NgsReportsToStrings")
    StudyInclusionRequestDto toDto(StudyInclusionRequest studyInclusionRequest);

    @Mapping(target = "episode.id", source = "episodeId")
    @Mapping(target = "diagnose.id", source = "reason")
    @Mapping(target = "ngsReports", source = "ngsReports", qualifiedByName = "StringsToNgsReports")
    StudyInclusionRequest toEntity(StudyInclusionRequestDto studyInclusionRequestDto);
}
