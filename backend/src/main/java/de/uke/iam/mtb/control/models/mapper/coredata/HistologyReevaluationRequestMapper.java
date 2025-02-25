package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.HistologyReevaluationRequestDto;
import de.uke.iam.mtb.control.models.coredata.HistologyReevaluationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistologyReevaluationRequestMapper {

    @Mapping(target = "episodeId", source = "episode.id")
    @Mapping(target = "specimen", source = "specimen.id")
    HistologyReevaluationRequestDto toDto(HistologyReevaluationRequest histologyReevaluationRequest);

    @Mapping(target = "episode.id", source = "episodeId")
    @Mapping(target = "specimen.id", source = "specimen")
    HistologyReevaluationRequest toEntity(HistologyReevaluationRequestDto histologyReevaluationRequestDto);

}
