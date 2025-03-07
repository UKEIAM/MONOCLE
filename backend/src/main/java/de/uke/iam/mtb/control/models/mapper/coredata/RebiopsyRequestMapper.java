package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.RebiopsyRequestDto;
import de.uke.iam.mtb.control.models.coredata.RebiopsyRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RebiopsyRequestMapper {

    @Mapping(target = "episodeId", source = "episode.id")
    @Mapping(target = "specimen", source = "specimen.id")
    RebiopsyRequestDto toDto(RebiopsyRequest rebiopsyRequest);

    @Mapping(target = "episode.id", source = "episodeId")
    @Mapping(target = "specimen.id", source = "specimen")
    RebiopsyRequest toEntity(RebiopsyRequestDto rebiopsyRequestDto);

}
