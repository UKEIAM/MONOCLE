package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.api.model.AuditTrailEntryDto;
import de.uke.iam.mtb.control.models.AuditTrailEntry;
import de.uke.iam.mtb.control.models.mapper.AuditTrailEntryMapper;
import de.uke.iam.mtb.control.repository.AuditTrailRepository;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.security.enums.JwtClaim;
import java.time.Instant;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AuditTrailService {

  private final AuditTrailRepository auditTrailRepository;
  private final AuditTrailEntryMapper auditTrailEntryMapper;

  public AuditTrailService(AuditTrailRepository auditTrailRepository, AuditTrailEntryMapper auditTrailEntryMapper) {
    this.auditTrailRepository = auditTrailRepository;
    this.auditTrailEntryMapper = auditTrailEntryMapper;
  }

  @Deprecated
  public void addEntry(String userId, String entry) {
    auditTrailRepository.save(new AuditTrailEntry(null, Instant.now(), userId, entry));
  }

  public void addEntry(JwtClaimMap jwtClaimMap, String entry) {
    String username = (String) jwtClaimMap.get(JwtClaim.USERNAME.getText());
    if (StringUtils.isBlank(username)) {
      username = "UNKNOWN USER";
    }
    auditTrailRepository.save(new AuditTrailEntry(null, Instant.now(), username, entry));
  }

  public List<AuditTrailEntryDto> getAllEntries() {
    return auditTrailRepository.findAll().stream().map(auditTrailEntryMapper::toDto).toList();
  }
}
