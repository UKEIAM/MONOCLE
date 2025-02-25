package de.uke.iam.mtb.control.rest;

import static de.uke.iam.mtb.control.helper.AudittrailHelper.getCurrentMethodName;

import de.uke.iam.mtb.api.model.AddressbookEntryDto;
import de.uke.iam.mtb.api.server.AddressbookentryApi;
import de.uke.iam.mtb.control.security.JwtClaimMap;
import de.uke.iam.mtb.control.service.AddressbookService;
import de.uke.iam.mtb.control.service.AuditTrailService;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AddressbookController implements AddressbookentryApi {

    private final AddressbookService addressbookService;
    private final AuditTrailService auditTrailService;

    public AddressbookController(AddressbookService addressbookService, AuditTrailService auditTrailService) {
        this.addressbookService = addressbookService;
        this.auditTrailService = auditTrailService;
    }

    @Override
    @Secured({"ROLE_MTBDOCTOR"})
    public ResponseEntity<List<AddressbookEntryDto>> getAddressbook() {
        return ResponseEntity.ok(addressbookService.getAllEntries());
    }


    @Override
    @Secured({"ROLE_MTBDOCTOR"})
    public ResponseEntity<AddressbookEntryDto> addAddressbookEntry(@Valid @RequestBody AddressbookEntryDto addressbookEntryDto) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        try {
            AddressbookEntryDto addedAddressbookEntryDto = addressbookService.addAddressbookEntry(addressbookEntryDto);
            auditTrailService.addEntry(jwtClaimMap,
                getCurrentMethodName() + " addAdressbook: " + addedAddressbookEntryDto.getId().toString());
            return ResponseEntity.ok(addedAddressbookEntryDto);
        } catch (IllegalArgumentException e) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed with IllegalArgumentException");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Override
    @Secured({"ROLE_MTBDOCTOR"})
    public ResponseEntity<Void> deleteAddressbookEntry(UUID id) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        try {
            addressbookService.deleteAddressbookEntry(id);
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " delete by ID: " + id.toString());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed with IllegalArgumentException");
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @Secured({"ROLE_MTBDOCTOR"})
    public ResponseEntity<AddressbookEntryDto> getAddressbookEntry(UUID id) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, "getAddressbookEntry: " + id.toString());

        AddressbookEntryDto addressbookEntryByID = addressbookService.getAddressbookEntry(id);

        if (addressbookEntryByID == null) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, entry not found");
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(addressbookEntryByID);
    }

    @Override
    @Secured({"ROLE_MTBDOCTOR"})
    public ResponseEntity<Void> updateAddressbookEntry(UUID id, @Valid @RequestBody AddressbookEntryDto addressbookEntryDto) {
        JwtClaimMap jwtClaimMap = new JwtClaimMap((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " update by ID: " + id.toString());

        if (!id.equals(addressbookEntryDto.getId())) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, ID in path and body do not match");
            return ResponseEntity.badRequest().build();
        }

        if (!addressbookService.updateAddressbookEntry(id, addressbookEntryDto)) {
            auditTrailService.addEntry(jwtClaimMap, getCurrentMethodName() + " failed, entry not found");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}
