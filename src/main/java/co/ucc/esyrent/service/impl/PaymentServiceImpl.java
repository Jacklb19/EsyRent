package co.ucc.esyrent.service.impl;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.Payment;
import co.ucc.esyrent.domain.strategy.LateFeeStrategy;
import co.ucc.esyrent.domain.valueobject.MoneyAmount;
import co.ucc.esyrent.dto.request.RegisterPaymentRequest;
import co.ucc.esyrent.dto.response.PaymentResponse;
import co.ucc.esyrent.exception.BusinessRuleException;
import co.ucc.esyrent.exception.ResourceNotFoundException;
import co.ucc.esyrent.mapper.PaymentMapper;
import co.ucc.esyrent.repository.ContractRepository;
import co.ucc.esyrent.repository.PaymentRepository;
import co.ucc.esyrent.service.PaymentService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.ucc.esyrent.service.EmailNotificationService;

@Service
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ContractRepository contractRepository;
    private final PaymentMapper paymentMapper;
    private final LateFeeStrategy lateFeeStrategy;
    private final EmailNotificationService emailNotificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, ContractRepository contractRepository,
                              PaymentMapper paymentMapper, LateFeeStrategy lateFeeStrategy,
                              EmailNotificationService emailNotificationService) {
        this.paymentRepository = paymentRepository;
        this.contractRepository = contractRepository;
        this.paymentMapper = paymentMapper;
        this.lateFeeStrategy = lateFeeStrategy;
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    @Transactional
    public PaymentResponse registerPayment(RegisterPaymentRequest request) {
        Contract contract = findContractById(request.contractId());

        paymentRepository.findByContractAndPaymentMonth(contract, request.paymentMonth())
                .ifPresent(existingPayment -> {
                    throw new BusinessRuleException("Payment for month " + request.paymentMonth() + " already exists");
                });

        Payment payment = new Payment(
                contract,
                request.paymentMonth(),
                new MoneyAmount(request.amount(), request.currency()).normalizeScale(),
                request.paymentDate()
        );
        payment.computeAndApplyLateFee(contract.getCutoff(), contract.getMonthlyRent(), lateFeeStrategy, request.paymentMonth());
        Payment savedPayment = paymentRepository.save(payment);
        emailNotificationService.sendPaymentRegisteredNotification(savedPayment.getId());
        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByContract(Long contractId) {
        Contract contract = findContractById(contractId);
        return paymentRepository.findByContract(contract).stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    private Contract findContractById(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract with id " + contractId + " was not found"));
    }
}
