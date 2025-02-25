package de.uke.iam.mtb.control.rest;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.TransferJobDto;
import de.uke.iam.mtb.api.server.BwhcTransferApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.BwhcTransferService;
import de.uke.iam.mtb.control.service.EpisodeService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BwhcTransferController implements BwhcTransferApi {

    BwhcTransferService bwhcTransferService;
    private final AuditTrailService auditTrailService;
    private final EpisodeService episodeService;

    public BwhcTransferController(BwhcTransferService bwhcTransferService, AuditTrailService auditTrailService,
        EpisodeService episodeService) {
        this.bwhcTransferService = bwhcTransferService;
        this.auditTrailService = auditTrailService;
        this.episodeService = episodeService;
    }

    @Override
    @Secured({"ROLE_MTBDOCTOR"})
    public ResponseEntity<TransferJobDto> addBwhcTransfer(@RequestParam("episodeId") UUID episodeId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap(
            (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " add transfer by EpisodeId: " + episodeId.toString());

        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, episode with ID " + episodeId + " does not exist");
            try {
                throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
            } catch (ForeignKeyException e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.ok(bwhcTransferService.addTransferJob(episodeId));
    }
}
