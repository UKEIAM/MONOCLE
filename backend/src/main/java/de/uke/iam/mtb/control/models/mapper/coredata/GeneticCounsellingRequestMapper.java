package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.GeneticCounsellingRequestDto;
import de.uke.iam.mtb.control.models.coredata.GeneticCounsellingRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GeneticCounsellingRequestMapper {

    @Mapping(target = "episodeId", source = "episode.id")
    GeneticCounsellingRequestDto toDto(GeneticCounsellingRequest geneticCounsellingRequest);

    @Mapping(target = "episode.id", source = "episodeId")
    GeneticCounsellingRequest toEntity(GeneticCounsellingRequestDto geneticCounsellingRequestDto);

}
