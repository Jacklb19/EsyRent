package co.ucc.esyrent.controller;

import co.ucc.esyrent.dto.request.ApproveRentalApplicationRequest;
import co.ucc.esyrent.dto.request.CreateRentalApplicationRequest;
import co.ucc.esyrent.dto.request.RejectRentalApplicationRequest;
import co.ucc.esyrent.dto.response.RentalApplicationResponse;
import co.ucc.esyrent.security.SecurityAccessService;
import co.ucc.esyrent.service.RentalApplicationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rental-applications")
public class RentalApplicationController {

    private final RentalApplicationService rentalApplicationService;
    private final SecurityAccessService securityAccessService;

    public RentalApplicationController(RentalApplicationService rentalApplicationService,
                                       SecurityAccessService securityAccessService) {
        this.rentalApplicationService = rentalApplicationService;
        this.securityAccessService = securityAccessService;
    }

    @PostMapping
    @PreAuthorize("@securityAccessService.canCreateRentalApplication(#request.propertyId(), authentication)")
    public ResponseEntity<RentalApplicationResponse> createApplication(
            @Valid @RequestBody CreateRentalApplicationRequest request,
            Authentication authentication) {
        Long tenantId = securityAccessService.resolveCurrentUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rentalApplicationService.createApplication(request, tenantId));
    }

    @GetMapping
    @PreAuthorize("@securityAccessService.canQueryRentalApplications(#tenantId, #ownerId, authentication)")
    public ResponseEntity<List<RentalApplicationResponse>> getApplications(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Long ownerId) {
        if (tenantId != null) {
            return ResponseEntity.ok(rentalApplicationService.getApplicationsByTenant(tenantId));
        }
        return ResponseEntity.ok(rentalApplicationService.getApplicationsByOwner(ownerId));
    }

    @GetMapping("/{applicationId}")
    @PreAuthorize("@securityAccessService.canAccessRentalApplication(#applicationId, authentication)")
    public ResponseEntity<RentalApplicationResponse> getApplicationById(@PathVariable Long applicationId) {
        return ResponseEntity.ok(rentalApplicationService.getApplicationById(applicationId));
    }

    @PutMapping("/{applicationId}/approve")
    @PreAuthorize("@securityAccessService.canReviewRentalApplication(#applicationId, authentication)")
    public ResponseEntity<RentalApplicationResponse> approveApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody ApproveRentalApplicationRequest request) {
        return ResponseEntity.ok(rentalApplicationService.approveApplication(applicationId, request));
    }

    @PutMapping("/{applicationId}/reject")
    @PreAuthorize("@securityAccessService.canReviewRentalApplication(#applicationId, authentication)")
    public ResponseEntity<RentalApplicationResponse> rejectApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody(required = false) RejectRentalApplicationRequest request) {
        return ResponseEntity.ok(rentalApplicationService.rejectApplication(
                applicationId,
                request == null ? new RejectRentalApplicationRequest(null) : request));
    }

    @PutMapping("/{applicationId}/cancel")
    @PreAuthorize("@securityAccessService.canCancelRentalApplication(#applicationId, authentication)")
    public ResponseEntity<RentalApplicationResponse> cancelApplication(
            @PathVariable Long applicationId,
            Authentication authentication) {
        Long tenantId = securityAccessService.resolveCurrentUserId(authentication);
        return ResponseEntity.ok(rentalApplicationService.cancelApplication(applicationId, tenantId));
    }
}
