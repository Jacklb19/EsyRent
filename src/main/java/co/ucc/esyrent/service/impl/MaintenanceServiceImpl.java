package co.ucc.esyrent.service.impl;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.MaintenanceRequest;
import co.ucc.esyrent.dto.request.CreateMaintenanceRequest;
import co.ucc.esyrent.dto.response.MaintenanceResponse;
import co.ucc.esyrent.exception.ResourceNotFoundException;
import co.ucc.esyrent.mapper.MaintenanceMapper;
import co.ucc.esyrent.repository.ContractRepository;
import co.ucc.esyrent.repository.MaintenanceRequestRepository;
import co.ucc.esyrent.service.MaintenanceService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final ContractRepository contractRepository;
    private final MaintenanceMapper maintenanceMapper;

    public MaintenanceServiceImpl(MaintenanceRequestRepository maintenanceRequestRepository,
                                  ContractRepository contractRepository, MaintenanceMapper maintenanceMapper) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.contractRepository = contractRepository;
        this.maintenanceMapper = maintenanceMapper;
    }

    @Override
    @Transactional
    public MaintenanceResponse createMaintenanceRequest(CreateMaintenanceRequest request) {
        Contract contract = findContractById(request.contractId());
        MaintenanceRequest maintenanceRequest = new MaintenanceRequest(
                contract,
                request.description(),
                request.category(),
                request.urgency()
        );
        return maintenanceMapper.toResponse(maintenanceRequestRepository.save(maintenanceRequest));
    }

    @Override
    @Transactional
    public MaintenanceResponse advanceStatus(Long maintenanceRequestId) {
        MaintenanceRequest request = findMaintenanceById(maintenanceRequestId);
        request.advance();
        return maintenanceMapper.toResponse(request);
    }

    @Override
    public List<MaintenanceResponse> getByContract(Long contractId) {
        Contract contract = findContractById(contractId);
        return maintenanceRequestRepository.findByContract(contract).stream()
                .map(maintenanceMapper::toResponse)
                .toList();
    }

    private Contract findContractById(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract with id " + contractId + " was not found"));
    }

    private MaintenanceRequest findMaintenanceById(Long maintenanceRequestId) {
        return maintenanceRequestRepository.findById(maintenanceRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Maintenance request with id " + maintenanceRequestId + " was not found"));
    }
}
