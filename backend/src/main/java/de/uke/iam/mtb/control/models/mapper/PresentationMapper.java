package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.PresentationDto;
import de.uke.iam.mtb.control.models.Presentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PresentationMapper {

  @Mapping(target = "episodeId", source = "episode.id")
  PresentationDto toDto(Presentation presentation);

  @Mapping(target = "episode.id", source = "episodeId")
  Presentation toEntity(PresentationDto presentationDto);
}
