package co.ucc.esyrent.service.impl;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.domain.enums.ContractStatus;
import co.ucc.esyrent.domain.factory.ContractFactory;
import co.ucc.esyrent.domain.valueobject.CancellationDetails;
import co.ucc.esyrent.domain.valueobject.MoneyAmount;
import co.ucc.esyrent.domain.valueobject.PaymentCutoff;
import co.ucc.esyrent.dto.request.CancelContractRequest;
import co.ucc.esyrent.dto.request.CreateContractRequest;
import co.ucc.esyrent.dto.response.ContractResponse;
import co.ucc.esyrent.exception.BusinessRuleException;
import co.ucc.esyrent.exception.ResourceNotFoundException;
import co.ucc.esyrent.mapper.ContractMapper;
import co.ucc.esyrent.repository.ContractRepository;
import co.ucc.esyrent.repository.PropertyRepository;
import co.ucc.esyrent.repository.UserRepository;
import co.ucc.esyrent.service.ContractService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ContractServiceImpl implements ContractService {

    private static final List<ContractStatus> NON_CLOSED_STATUSES = List.of(
            ContractStatus.ACTIVE,
            ContractStatus.EXPIRING_SOON
    );

    private final ContractRepository contractRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ContractFactory contractFactory;
    private final ContractMapper contractMapper;

    public ContractServiceImpl(ContractRepository contractRepository, PropertyRepository propertyRepository,
                               UserRepository userRepository, ContractFactory contractFactory,
                               ContractMapper contractMapper) {
        this.contractRepository = contractRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.contractFactory = contractFactory;
        this.contractMapper = contractMapper;
    }

    @Override
    @Transactional
    public ContractResponse createContract(CreateContractRequest request) {
        Property property = findPropertyById(request.propertyId());
        User tenant = findUserById(request.tenantId());

        if (contractRepository.existsByPropertyAndStatusIn(property, NON_CLOSED_STATUSES)) {
            throw new BusinessRuleException("Property already has an active contract");
        }

        Contract contract = contractFactory.createContract(
                property,
                tenant,
                request.startDate(),
                request.durationMonths(),
                new MoneyAmount(request.monthlyRentAmount(), request.monthlyRentCurrency()).normalizeScale(),
                new PaymentCutoff(request.cutoffDay()),
                new MoneyAmount(request.depositAmount(), request.depositCurrency()).normalizeScale()
        );
        return contractMapper.toResponse(contractRepository.save(contract));
    }

    @Override
    public ContractResponse getContractById(Long contractId) {
        return contractMapper.toResponse(findContractById(contractId));
    }

    @Override
    public List<ContractResponse> getContractsByTenant(Long tenantId) {
        User tenant = findUserById(tenantId);
        return contractRepository.findByTenant(tenant).stream()
                .map(contractMapper::toResponse)
                .toList();
    }

    @Override
    public List<ContractResponse> getContractsByProperty(Long propertyId) {
        Property property = findPropertyById(propertyId);
        return contractRepository.findByProperty(property).stream()
                .map(contractMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ContractResponse cancelContract(Long contractId, CancelContractRequest request) {
        Contract contract = findContractById(contractId);
        contract.cancel(new CancellationDetails(request.reason(), request.date()));
        return contractMapper.toResponse(contract);
    }

    private Contract findContractById(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract with id " + contractId + " was not found"));
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
