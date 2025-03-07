package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.CommentDto;
import de.uke.iam.mtb.control.models.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

  @Mapping(target = "patientId", source = "patient.id")
  CommentDto toDto(Comment comment);

  @Mapping(target = "patient.id", source = "patientId")
  Comment toEntity(CommentDto commentDto);

}
