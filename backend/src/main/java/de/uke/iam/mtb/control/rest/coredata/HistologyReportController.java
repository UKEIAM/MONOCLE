package de.uke.iam.mtb.control.rest.coredata;

import de.uke.iam.mtb.api.model.HistologyReportDto;
import de.uke.iam.mtb.api.server.HistologyReportApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.coredata.HistologyReportService;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HistologyReportController implements HistologyReportApi {

  public HistologyReportService histologyReportService;
  public AuditTrailService auditTrailService;

  public HistologyReportController(HistologyReportService histologyReportService, AuditTrailService auditTrailService) {
    this.histologyReportService = histologyReportService;
    this.auditTrailService = auditTrailService;
  }

  public HistologyReportDto checkTumorMorphologyAndTumorCellContent(HistologyReportDto histologyReportDto) throws ForeignKeyException {
    // check the id in TumorMorphology if not set then set a random id
    if (histologyReportDto.getTumorMorphology() != null) {
      if (histologyReportDto.getTumorMorphology().getId() == null) {
        histologyReportDto.getTumorMorphology().setId(UUID.randomUUID());
      }
      // check if the specimen and episodeId are the same as the histologyReportDto
      if (!histologyReportDto.getTumorMorphology().getSpecimen().equals(histologyReportDto.getSpecimen()) ||
          !histologyReportDto.getTumorMorphology().getEpisodeId().equals(histologyReportDto.getEpisodeId())) {
        throw new ForeignKeyException(
            "Specimen or Episode with ID " + histologyReportDto.getSpecimen() + " or " + histologyReportDto.getEpisodeId()
                + " not identical");
      }
    }
    // check the id in TumorCellContent if not set then set a random id
    if (histologyReportDto.getTumorCellContent() != null) {
      if (histologyReportDto.getTumorCellContent().getId() == null) {
        histologyReportDto.getTumorCellContent().setId(UUID.randomUUID());
      }
      // check if the specimen are the same as the histologyReportDto
      if (!histologyReportDto.getTumorCellContent().getSpecimen().equals(histologyReportDto.getSpecimen())) {
        throw new ForeignKeyException("Specimen with ID " + histologyReportDto.getSpecimen() + " not identical");
      }
    }
    return histologyReportDto;
  }

  public ResponseEntity<HistologyReportDto> addHistologyReport(@PathVariable("episodeId") UUID episodeId,
      @Valid @RequestBody HistologyReportDto histologyReportDto) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

    if (!episodeId.equals(histologyReportDto.getEpisodeId())) {
      auditTrailService.addEntry(jwtClaimMap, "histologyReport episode Id does not match with: " + episodeId);
      return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    try {
      auditTrailService.addEntry(jwtClaimMap, "add histology report to episode with ID " + episodeId);
      histologyReportDto = checkTumorMorphologyAndTumorCellContent(histologyReportDto);
      return ResponseEntity.ok(histologyReportService.addHistologyReport(histologyReportDto));
    } catch (ForeignKeyException exception) {
      auditTrailService.addEntry(jwtClaimMap, "failed to save histology report for episode: " + episodeId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  public ResponseEntity<List<HistologyReportDto>> getAllHistologyReports(@PathVariable("episodeId") UUID episodeId) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

    try {
      auditTrailService.addEntry(jwtClaimMap, "get all Histology reports by episode with ID " + episodeId);
      return ResponseEntity.ok(histologyReportService.getAllHistologyReports(episodeId));
    } catch (ForeignKeyException exception) {
      auditTrailService.addEntry(jwtClaimMap, "failed to get Histology reports for episode: " + episodeId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

  }

  public ResponseEntity<HistologyReportDto> getHistologyReport(@PathVariable("episodeId") UUID episodeId,
      @PathVariable("histologyReportId") UUID histologyReportId) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

    if (!histologyReportService.isHistologyReportExist(histologyReportId)) {
      auditTrailService.addEntry(jwtClaimMap, "no Histology report found for episode: " + episodeId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    HistologyReportDto histologyReportDto = histologyReportService.getHistologyReport(histologyReportId);

    if (!episodeId.equals(histologyReportDto.getEpisodeId())) {
      auditTrailService.addEntry(jwtClaimMap, "histologyReport episode Id does not match with: " + episodeId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    auditTrailService.addEntry(jwtClaimMap, "get Specimen by ID " + histologyReportId);
    return ResponseEntity.ok(histologyReportService.getHistologyReport(histologyReportId));
  }

  public ResponseEntity<HistologyReportDto> updateHistologyReport(@PathVariable("episodeId") UUID episodeId
      , @PathVariable("histologyReportId") UUID histologyReportId, @Valid @RequestBody HistologyReportDto histologyReportDto) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    if (!histologyReportService.isHistologyReportExist(histologyReportId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    HistologyReportDto savedHistologyReportDto = histologyReportService.getHistologyReport(histologyReportId);

    if (!histologyReportDto.getEpisodeId().equals(savedHistologyReportDto.getEpisodeId()) || !episodeId.equals(
        histologyReportDto.getEpisodeId())) {
      auditTrailService.addEntry(jwtClaimMap, "histologyReport episode Id does not match with: " + episodeId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      auditTrailService.addEntry(jwtClaimMap, "update HistologyReport by ID " + histologyReportId);
      histologyReportDto = checkTumorMorphologyAndTumorCellContent(histologyReportDto);
      return ResponseEntity.ok(histologyReportService.updateHistologyReport(histologyReportId, histologyReportDto));
    } catch (ForeignKeyException exception) {
      auditTrailService.addEntry(jwtClaimMap, "failed to get Histology reports for episode: " + episodeId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

  }

  public ResponseEntity<Void> deleteHistologyReport(@PathVariable("episodeId") UUID episodeId,
      @PathVariable("histologyReportId") UUID histologyReportId) {

    JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    if (!histologyReportService.isHistologyReportExist(histologyReportId)) {
      auditTrailService.addEntry(jwtClaimMap, "no Histology report found for episode: " + episodeId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    try {
      histologyReportService.deleteHistologyReport(histologyReportId);
      auditTrailService.addEntry(jwtClaimMap, "delete HistologyReport by ID " + histologyReportId);
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (Exception exception) {
      auditTrailService.addEntry(jwtClaimMap, "failed to get Histology reports for episode: " + episodeId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

  }
}
