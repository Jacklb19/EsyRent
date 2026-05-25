package co.ucc.esyrent.service.impl;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.domain.entity.RentalApplication;
import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.domain.enums.ContractStatus;
import co.ucc.esyrent.domain.enums.RentalApplicationStatus;
import co.ucc.esyrent.dto.request.ApproveRentalApplicationRequest;
import co.ucc.esyrent.dto.request.CreateContractRequest;
import co.ucc.esyrent.dto.request.CreateRentalApplicationRequest;
import co.ucc.esyrent.dto.request.RejectRentalApplicationRequest;
import co.ucc.esyrent.dto.response.ContractResponse;
import co.ucc.esyrent.dto.response.RentalApplicationResponse;
import co.ucc.esyrent.exception.BusinessRuleException;
import co.ucc.esyrent.exception.ResourceNotFoundException;
import co.ucc.esyrent.mapper.RentalApplicationMapper;
import co.ucc.esyrent.repository.ContractRepository;
import co.ucc.esyrent.repository.PropertyRepository;
import co.ucc.esyrent.repository.RentalApplicationRepository;
import co.ucc.esyrent.repository.UserRepository;
import co.ucc.esyrent.service.ContractService;
import co.ucc.esyrent.service.RentalApplicationService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RentalApplicationServiceImpl implements RentalApplicationService {

    private static final List<ContractStatus> ACTIVE_CONTRACT_STATUSES = List.of(
            ContractStatus.ACTIVE,
            ContractStatus.EXPIRING_SOON
    );

    private static final List<RentalApplicationStatus> OPEN_APPLICATION_STATUSES = List.of(
            RentalApplicationStatus.PENDING
    );

    private final RentalApplicationRepository rentalApplicationRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final ContractService contractService;
    private final RentalApplicationMapper rentalApplicationMapper;

    public RentalApplicationServiceImpl(RentalApplicationRepository rentalApplicationRepository,
                                        PropertyRepository propertyRepository,
                                        UserRepository userRepository,
                                        ContractRepository contractRepository,
                                        ContractService contractService,
                                        RentalApplicationMapper rentalApplicationMapper) {
        this.rentalApplicationRepository = rentalApplicationRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
        this.contractService = contractService;
        this.rentalApplicationMapper = rentalApplicationMapper;
    }

    @Override
    @Transactional
    public RentalApplicationResponse createApplication(CreateRentalApplicationRequest request, Long tenantId) {
        Property property = findPropertyById(request.propertyId());
        User tenant = findUserById(tenantId);

        if (!tenant.isTenant()) {
            throw new BusinessRuleException("Only tenants can submit rental applications");
        }
        if (property.getOwner().getId().equals(tenant.getId())) {
            throw new BusinessRuleException("You cannot apply to rent your own property");
        }
        if (!property.isAvailable()) {
            throw new BusinessRuleException("Property is not available for rental");
        }
        if (rentalApplicationRepository.existsByPropertyAndTenantAndStatus(
                property, tenant, RentalApplicationStatus.PENDING)) {
            throw new BusinessRuleException("You already have a pending application for this property");
        }
        if (contractRepository.existsByPropertyAndTenantAndStatusIn(
                property, tenant, ACTIVE_CONTRACT_STATUSES)) {
            throw new BusinessRuleException("You already have an active contract for this property");
        }
        if (contractRepository.existsByPropertyAndStatusIn(property, ACTIVE_CONTRACT_STATUSES)) {
            throw new BusinessRuleException("Property already has an active contract");
        }

        RentalApplication application = new RentalApplication(property, tenant, request.message());
        return rentalApplicationMapper.toResponse(rentalApplicationRepository.save(application));
    }

    @Override
    public RentalApplicationResponse getApplicationById(Long applicationId) {
        return rentalApplicationMapper.toResponse(findApplicationById(applicationId));
    }

    @Override
    public List<RentalApplicationResponse> getApplicationsByTenant(Long tenantId) {
        User tenant = findUserById(tenantId);
        return rentalApplicationRepository.findByTenantOrderBySubmittedAtDesc(tenant).stream()
                .map(rentalApplicationMapper::toResponse)
                .toList();
    }

    @Override
    public List<RentalApplicationResponse> getApplicationsByOwner(Long ownerId) {
        User owner = findUserById(ownerId);
        return rentalApplicationRepository.findByProperty_OwnerOrderBySubmittedAtDesc(owner).stream()
                .map(rentalApplicationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RentalApplicationResponse approveApplication(Long applicationId, ApproveRentalApplicationRequest request) {
        RentalApplication application = findApplicationById(applicationId);
        if (application.getStatus() != RentalApplicationStatus.PENDING) {
            throw new BusinessRuleException("Only pending applications can be approved");
        }

        Property property = application.getProperty();
        User tenant = application.getTenant();

        if (!property.isAvailable()) {
            throw new BusinessRuleException("Property is no longer available");
        }
        if (contractRepository.existsByPropertyAndStatusIn(property, ACTIVE_CONTRACT_STATUSES)) {
            throw new BusinessRuleException("Property already has an active contract");
        }

        CreateContractRequest contractRequest = new CreateContractRequest(
                property.getId(),
                tenant.getId(),
                request.startDate(),
                request.durationMonths(),
                request.monthlyRentAmount(),
                request.monthlyRentCurrency(),
                request.cutoffDay(),
                request.depositAmount(),
                request.depositCurrency()
        );
        ContractResponse contractResponse = contractService.createContract(contractRequest);
        Contract contract = contractRepository.findById(contractResponse.id())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contract with id " + contractResponse.id() + " was not found"));

        application.approve(contract);
        return rentalApplicationMapper.toResponse(application);
    }

    @Override
    @Transactional
    public RentalApplicationResponse rejectApplication(Long applicationId, RejectRentalApplicationRequest request) {
        RentalApplication application = findApplicationById(applicationId);
        application.reject(request == null ? null : request.reason());
        return rentalApplicationMapper.toResponse(application);
    }

    @Override
    @Transactional
    public RentalApplicationResponse cancelApplication(Long applicationId, Long tenantId) {
        RentalApplication application = findApplicationById(applicationId);
        if (!application.getTenant().getId().equals(tenantId)) {
            throw new BusinessRuleException("You can only cancel your own applications");
        }
        application.cancel();
        return rentalApplicationMapper.toResponse(application);
    }

    private RentalApplication findApplicationById(Long applicationId) {
        return rentalApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rental application with id " + applicationId + " was not found"));
    }

    private Property findPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property with id " + propertyId + " was not found"));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " was not found"));
    }
}
