package co.ucc.esyrent.repository;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.Payment;
import co.ucc.esyrent.domain.enums.PaymentStatus;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByContract(Contract contract);

    Optional<Payment> findByContractAndPaymentMonth(Contract contract, YearMonth paymentMonth);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByPaymentMonth(YearMonth paymentMonth);
}
