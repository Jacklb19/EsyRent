package co.ucc.esyrent.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.Payment;
import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.domain.enums.PaymentStatus;
import co.ucc.esyrent.domain.enums.PropertyType;
import co.ucc.esyrent.domain.enums.UserRole;
import co.ucc.esyrent.domain.strategy.LateFeeStrategy;
import co.ucc.esyrent.domain.valueobject.MoneyAmount;
import co.ucc.esyrent.domain.valueobject.PaymentCutoff;
import co.ucc.esyrent.dto.request.RegisterPaymentRequest;
import co.ucc.esyrent.dto.response.PaymentResponse;
import co.ucc.esyrent.exception.BusinessRuleException;
import co.ucc.esyrent.mapper.PaymentMapper;
import co.ucc.esyrent.repository.ContractRepository;
import co.ucc.esyrent.repository.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ContractRepository contractRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private LateFeeStrategy lateFeeStrategy;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Contract contract;
    private RegisterPaymentRequest request;

    @BeforeEach
    void setUp() {
        User owner = new User("Owner", "owner@test.com", "encoded", "300", UserRole.OWNER);
        Property property = new Property(
                owner,
                "Street 123",
                PropertyType.HOUSE,
                new BigDecimal("120"),
                new MoneyAmount(new BigDecimal("2000.00"), "USD"),
                "House"
        );
        User tenant = new User("Tenant", "tenant@test.com", "encoded", "301", UserRole.TENANT);
        contract = new Contract(
                property,
                tenant,
                LocalDate.of(2026, 5, 1),
                12,
                new MoneyAmount(new BigDecimal("2000.00"), "USD"),
                new PaymentCutoff(10),
                new MoneyAmount(new BigDecimal("2000.00"), "USD")
        );
        request = new RegisterPaymentRequest(
                1L,
                YearMonth.of(2026, 5),
                new BigDecimal("2000.00"),
                "USD",
                LocalDate.of(2026, 5, 8)
        );
    }

    @Test
    void shouldRejectDuplicatePaymentForSameMonth() {
        Payment existingPayment = new Payment(
                contract,
                request.paymentMonth(),
                new MoneyAmount(request.amount(), request.currency()),
                request.paymentDate()
        );

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(paymentRepository.findByContractAndPaymentMonth(contract, request.paymentMonth()))
                .thenReturn(Optional.of(existingPayment));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> paymentService.registerPayment(request)
        );

        assertEquals("Payment for month 2026-05 already exists", exception.getMessage());
    }

    @Test
    void shouldRegisterPaymentAndApplyLateFeeStrategy() {
        Payment savedPayment = new Payment(
                contract,
                request.paymentMonth(),
                new MoneyAmount(request.amount(), request.currency()),
                request.paymentDate()
        );
        PaymentResponse response = new PaymentResponse(
                20L, 1L, request.paymentMonth(), request.amount(), request.currency(),
                request.paymentDate(), PaymentStatus.ON_TIME, null, null
        );

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(paymentRepository.findByContractAndPaymentMonth(contract, request.paymentMonth())).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(response);

        PaymentResponse result = paymentService.registerPayment(request);

        assertEquals(20L, result.id());
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentMapper).toResponse(any(Payment.class));
    }
}
