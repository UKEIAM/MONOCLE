package de.uke.iam.mtb.control.rest;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.StepInfoDto;
import de.uke.iam.mtb.api.server.StepsinfoApi;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AuditTrailService;
import de.uke.iam.mtb.control.service.EpisodeService;
import de.uke.iam.mtb.control.service.StepInfoService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Secured({"ROLE_MTBDOCTOR"})
public class StepsInfoController implements StepsinfoApi {

    private final AuditTrailService auditTrailService;
    public StepInfoService stepInfoService;
    public EpisodeService episodeService;

    public StepsInfoController(EpisodeService episodeService, StepInfoService stepInfoService, AuditTrailService auditTrailService) {
        this.episodeService = episodeService;
        this.stepInfoService = stepInfoService;
        this.auditTrailService = auditTrailService;
    }

    @Override
    public ResponseEntity<List<StepInfoDto>> getStepsInfo(@PathVariable("id") UUID episodeId) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " get all by episode ID " + episodeId);

        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, episode with ID " + episodeId + " does not exist");
            return ResponseEntity.notFound().build();
        }
        List<StepInfoDto> stepsInfo = stepInfoService.getStepsInfoByEpisodeId(episodeId);
        return ResponseEntity.ok(stepsInfo);
    }

    @Override
    public ResponseEntity<List<StepInfoDto>> updateStepsInfo(@PathVariable("id") UUID episodeId,
        @RequestBody List<StepInfoDto> stepInfoDto) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by episode ID " + episodeId);
        if (!episodeService.isEpisodeExist(episodeId)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, episode with ID " + episodeId + " does not exist");
            return ResponseEntity.notFound().build();
        }
        try {
            stepInfoService.updateStepsInfo(episodeId, stepInfoDto);
            return ResponseEntity.ok(stepInfoDto);
        } catch (ForeignKeyException e) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, ForeignKeyException with episode ID " + episodeId);
            return ResponseEntity.badRequest().build();
        }
    }

}
