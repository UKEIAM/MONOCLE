package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.MolecularTherapyResponseDto;
import de.uke.iam.mtb.control.models.coredata.MolecularTherapyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MolecularTherapyResponseMapper {

    @Mapping(target = "episodeId", source = "episode.id")
    @Mapping(target = "therapy", source = "therapy.id")
    MolecularTherapyResponseDto toDto(MolecularTherapyResponse molecularTherapyResponse);

    @Mapping(target = "episode.id", source = "episodeId")
    @Mapping(target = "therapy.id", source = "therapy")
    MolecularTherapyResponse toEntity(MolecularTherapyResponseDto molecularTherapyResponseDto);

}
