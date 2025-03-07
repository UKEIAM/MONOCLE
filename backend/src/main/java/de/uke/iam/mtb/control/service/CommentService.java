package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.api.model.CommentDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Comment;
import de.uke.iam.mtb.control.models.Patient;
import de.uke.iam.mtb.control.models.mapper.CommentMapper;
import de.uke.iam.mtb.control.repository.CommentRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

  private final CommentRepository commentRepository;
  private final PatientService patientService;
  private final CommentMapper commentMapper;

  public CommentService(CommentRepository commentRepository, PatientService patientService, CommentMapper commentMapper) {
    this.commentRepository = commentRepository;
    this.patientService = patientService;
    this.commentMapper = commentMapper;
  }

  public List<CommentDto> getAllComments() {
    return commentRepository.findAll().stream().map(commentMapper::toDto).toList();
  }

  public List<CommentDto> getUndeletedCommentsByPatientID(UUID patientId) {
    return commentRepository.findAllByPatientId(patientId).stream().filter(patient -> patient.getDeletedAt() == null)
        .map(commentMapper::toDto).toList();
  }

  public void deleteComment(UUID commentId) {
    commentRepository.deleteById(commentId);
  }

  public CommentDto addCommentByPatientID(UUID patientId, CommentDto commentDto) throws ForeignKeyException {
    try {
      Patient savedPatientReference = patientService.getPatientReference(patientId);
      Comment comment = commentMapper.toEntity(commentDto);
      comment.setPatient(savedPatientReference);
      return commentMapper.toDto(commentRepository.save(comment));
    } catch (DataIntegrityViolationException e) {
      throw new ForeignKeyException("Patient with ID " + patientId + " does not exist");
    }
  }

  public CommentDto updateComment(UUID commentId, CommentDto updatedCommentDto) {
    Comment existingComment = commentRepository.findById(commentId).orElse(null);
    if (existingComment != null && updatedCommentDto.getComment() != null && updatedCommentDto.getHighlighted() != null) {
      existingComment.setComment(updatedCommentDto.getComment());
      existingComment.setHighlighted(updatedCommentDto.getHighlighted());
      return commentMapper.toDto(commentRepository.save(existingComment));
    } else {
      return null;
    }
  }

  public boolean isCommentExist(UUID commentId) {
    return commentRepository.existsById(commentId);
  }

}
