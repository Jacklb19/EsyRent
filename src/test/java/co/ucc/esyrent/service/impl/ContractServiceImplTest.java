package co.ucc.esyrent.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.domain.enums.ContractStatus;
import co.ucc.esyrent.domain.enums.PropertyType;
import co.ucc.esyrent.domain.enums.UserRole;
import co.ucc.esyrent.domain.factory.ContractFactory;
import co.ucc.esyrent.domain.valueobject.MoneyAmount;
import co.ucc.esyrent.dto.request.CreateContractRequest;
import co.ucc.esyrent.dto.response.ContractResponse;
import co.ucc.esyrent.exception.BusinessRuleException;
import co.ucc.esyrent.mapper.ContractMapper;
import co.ucc.esyrent.repository.ContractRepository;
import co.ucc.esyrent.repository.PropertyRepository;
import co.ucc.esyrent.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {

    @Mock
    private ContractRepository contractRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ContractFactory contractFactory;
    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private ContractServiceImpl contractService;

    private Property property;
    private User tenant;
    private Contract contract;
    private CreateContractRequest request;

    @BeforeEach
    void setUp() {
        User owner = new User("Owner", "owner@test.com", "encoded", "300", UserRole.OWNER);
        property = new Property(
                owner,
                "Street 123",
                PropertyType.APARTMENT,
                new BigDecimal("80"),
                new MoneyAmount(new BigDecimal("1500.00"), "USD"),
                "Nice apartment"
        );
        tenant = new User("Tenant", "tenant@test.com", "encoded", "301", UserRole.TENANT);
        contract = new Contract(
                property,
                tenant,
                LocalDate.of(2026, 5, 1),
                12,
                new MoneyAmount(new BigDecimal("1500.00"), "USD"),
                new co.ucc.esyrent.domain.valueobject.PaymentCutoff(10),
                new MoneyAmount(new BigDecimal("1500.00"), "USD")
        );
        request = new CreateContractRequest(
                1L,
                2L,
                LocalDate.of(2026, 6, 1),
                12,
                new BigDecimal("1500.00"),
                "USD",
                10,
                new BigDecimal("1500.00"),
                "USD"
        );
    }

    @Test
    void shouldRejectContractWhenPropertyAlreadyHasActiveContract() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(userRepository.findById(2L)).thenReturn(Optional.of(tenant));
        when(contractRepository.existsByPropertyAndStatusIn(any(Property.class), any(List.class))).thenReturn(true);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> contractService.createContract(request)
        );

        assertEquals("Property already has an active contract", exception.getMessage());
    }

    @Test
    void shouldCreateContractWhenPropertyIsAvailable() {
        ContractResponse response = new ContractResponse(
                10L, 1L, 2L, "Tenant",
                LocalDate.of(2026, 6, 1), LocalDate.of(2027, 6, 1), 12,
                new BigDecimal("1500.00"), "USD", 10,
                new BigDecimal("1500.00"), "USD",
                ContractStatus.ACTIVE, null, null
        );

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(userRepository.findById(2L)).thenReturn(Optional.of(tenant));
        when(contractRepository.existsByPropertyAndStatusIn(any(Property.class), any(List.class))).thenReturn(false);
        when(contractFactory.createContract(any(), any(), any(), any(), any(), any(), any())).thenReturn(contract);
        when(contractRepository.save(contract)).thenReturn(contract);
        when(contractMapper.toResponse(contract)).thenReturn(response);

        ContractResponse result = contractService.createContract(request);

        assertEquals(10L, result.id());
        assertEquals(ContractStatus.ACTIVE, result.status());
        verify(contractRepository).save(contract);
    }
}
