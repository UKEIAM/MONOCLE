package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.ClaimDto;
import de.uke.iam.mtb.control.models.coredata.Claim;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClaimMapper {

    @Mapping(target = "episodeId", source = "episode.id")
    @Mapping(target = "therapy", source = "therapyRecommendation.id")
    ClaimDto toDto(Claim claim);

    @Mapping(target = "episode.id", source = "episodeId")
    @Mapping(target = "therapyRecommendation.id", source = "therapy")
    Claim toEntity(ClaimDto claimDto);

}
