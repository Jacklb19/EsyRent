package co.ucc.esyrent.service;

import co.ucc.esyrent.dto.request.ApproveRentalApplicationRequest;
import co.ucc.esyrent.dto.request.CreateRentalApplicationRequest;
import co.ucc.esyrent.dto.request.RejectRentalApplicationRequest;
import co.ucc.esyrent.dto.response.RentalApplicationResponse;
import java.util.List;

public interface RentalApplicationService {

    RentalApplicationResponse createApplication(CreateRentalApplicationRequest request, Long tenantId);

    RentalApplicationResponse getApplicationById(Long applicationId);

    List<RentalApplicationResponse> getApplicationsByTenant(Long tenantId);

    List<RentalApplicationResponse> getApplicationsByOwner(Long ownerId);

    RentalApplicationResponse approveApplication(Long applicationId, ApproveRentalApplicationRequest request);

    RentalApplicationResponse rejectApplication(Long applicationId, RejectRentalApplicationRequest request);

    RentalApplicationResponse cancelApplication(Long applicationId, Long tenantId);
}
