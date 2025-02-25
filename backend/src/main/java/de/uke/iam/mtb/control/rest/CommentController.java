package de.uke.iam.mtb.control.rest;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.CommentDto;
import de.uke.iam.mtb.api.server.CommentsApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.helper.FullnameKeyCloakHelper;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.CommentService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CommentController implements CommentsApi {

  private final CommentService commentService;

  private final AuditTrailService auditTrailService;

  public CommentController(CommentService commentService, AuditTrailService auditTrailService) {
    this.commentService = commentService;
    this.auditTrailService = auditTrailService;
  }

  @Override
  @Secured({"ROLE_MTBDOCTOR"})
  public ResponseEntity<List<CommentDto>> getComments(@RequestParam(name = "patientId", required = false) UUID patientId) {
    // TODO: check if patient exists
    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    if (patientId == null) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get all comments ");
      return ResponseEntity.ok(commentService.getAllComments());
    } else {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get comments by PatientId: " + patientId);
      return ResponseEntity.ok(commentService.getUndeletedCommentsByPatientID(patientId));
    }
  }

  @Override
  @Secured({"ROLE_MTBDOCTOR"})
  public ResponseEntity<Void> addComment(@RequestParam("patientId") UUID patientId, CommentDto commentDto) {
    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add by PatientID: " + patientId.toString());
    try {
      FullnameKeyCloakHelper fullnameKeyCloakHelper = new FullnameKeyCloakHelper();
      String fullname = fullnameKeyCloakHelper.getFullnameFromKeyCloak();

      // the author username must come from the security token!
      commentDto.setAuthor(fullname);

      commentService.addCommentByPatientID(patientId, commentDto);
      return ResponseEntity.status(HttpStatus.OK).build();
      // Patient with the given Id does not exist
    } catch (ForeignKeyException e) {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, patient with ID " + patientId + " does not exist");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @Override
  @Secured({"ROLE_MTBDOCTOR"})
  public ResponseEntity<Void> deleteComment(UUID commentId) {
    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " delete by Id: "  + commentId.toString());
    if (commentService.isCommentExist(commentId)) {
      commentService.deleteComment(commentId);
      return ResponseEntity.status(HttpStatus.OK).build();
    } else {
      auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, comment with ID " + commentId + " does not exist");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @Override
  @Secured({"ROLE_MTBDOCTOR"})
  public ResponseEntity<Void> updateComment(UUID commentId, CommentDto commentDto) {
    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    auditTrailService.addEntry(jwtClaimMap,
        getCurrentMethodName() + " update by CommentID: " + commentId.toString());
    if (!commentService.isCommentExist(commentId)) {
      auditTrailService.addEntry(jwtClaimMap,
          getCurrentMethodName() + " failed, comment with ID " + commentId + " does not exist");
      return ResponseEntity.notFound().build();
    } else {
      commentService.updateComment(commentId, commentDto);
      return ResponseEntity.ok().build();
    }
  }
}
