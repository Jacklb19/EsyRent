package co.ucc.esyrent.service.impl;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.Payment;
import co.ucc.esyrent.exception.ResourceNotFoundException;
import co.ucc.esyrent.repository.ContractRepository;
import co.ucc.esyrent.repository.PaymentRepository;
import co.ucc.esyrent.service.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);

    private final JavaMailSender mailSender;
    private final ContractRepository contractRepository;
    private final PaymentRepository paymentRepository;

    public EmailNotificationServiceImpl(JavaMailSender mailSender, ContractRepository contractRepository,
                                        PaymentRepository paymentRepository) {
        this.mailSender = mailSender;
        this.contractRepository = contractRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void sendContractCreatedNotification(Long contractId) {
        Contract contract = findContractById(contractId);
        sendEmail(
                contract.getTenant().getEmail(),
                "EsyRent - Contrato creado",
                "Tu contrato para la propiedad en " + contract.getProperty().getAddress()
                        + " fue creado con exito."
        );
    }

    @Override
    public void sendContractExpiringNotification(Long contractId) {
        Contract contract = findContractById(contractId);
        String message = "El contrato de la propiedad en " + contract.getProperty().getAddress()
                + " esta proximo a vencer el " + contract.getEndDate() + ".";
        sendEmail(contract.getTenant().getEmail(), "EsyRent - Contrato proximo a vencer", message);
        sendEmail(contract.getProperty().getOwner().getEmail(), "EsyRent - Contrato proximo a vencer", message);
    }

    @Override
    public void sendPaymentRegisteredNotification(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment with id " + paymentId + " was not found"));
        sendEmail(
                payment.getContract().getTenant().getEmail(),
                "EsyRent - Pago registrado",
                "Se registro el pago del periodo " + payment.getPaymentMonth()
                        + " por valor de " + payment.getAmount().getAmount() + " "
                        + payment.getAmount().getCurrency() + "."
        );
    }

    private Contract findContractById(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract with id " + contractId + " was not found"));
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception exception) {
            LOGGER.warn("Email could not be sent to {} with subject '{}': {}", to, subject, exception.getMessage());
        }
    }
}
