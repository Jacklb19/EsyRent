package co.ucc.esyrent.service;

import co.ucc.esyrent.dto.request.CreateMaintenanceRequest;
import co.ucc.esyrent.dto.response.MaintenanceResponse;
import java.util.List;

public interface MaintenanceService {

    MaintenanceResponse createMaintenanceRequest(CreateMaintenanceRequest request);

    MaintenanceResponse advanceStatus(Long maintenanceRequestId);

    List<MaintenanceResponse> getByContract(Long contractId);
}
