package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.ClaimResponseDto;
import de.uke.iam.mtb.control.models.coredata.ClaimResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClaimResponseMapper {

  @Mapping(target = "episodeId", source = "episode.id")
  @Mapping(target = "claim", source = "claim.id")
  ClaimResponseDto toDto(ClaimResponse claimResponse);

  @Mapping(target = "episode.id", source = "episodeId")
  @Mapping(target = "claim.id", source = "claim")
  ClaimResponse toEntity(ClaimResponseDto claimResponseDto);

}
