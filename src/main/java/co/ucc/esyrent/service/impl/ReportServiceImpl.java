package co.ucc.esyrent.service.impl;

import co.ucc.esyrent.domain.entity.Payment;
import co.ucc.esyrent.dto.request.ReportFilter;
import co.ucc.esyrent.dto.response.MonthlyIncomeResponse;
import co.ucc.esyrent.dto.response.PaymentReportResponse;
import co.ucc.esyrent.repository.PaymentRepository;
import co.ucc.esyrent.service.ReportService;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final PaymentRepository paymentRepository;

    public ReportServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<PaymentReportResponse> generatePaymentReport(ReportFilter filter) {
        return filterPayments(filter).stream()
                .map(payment -> new PaymentReportResponse(
                        payment.getContract().getId(),
                        payment.getContract().getProperty().getId(),
                        payment.getContract().getProperty().getAddress(),
                        payment.getContract().getTenant().getId(),
                        payment.getContract().getTenant().getFullName(),
                        payment.getPaymentMonth(),
                        payment.getAmount().getAmount(),
                        payment.getLateFee() == null ? BigDecimal.ZERO : payment.getLateFee().getAmount(),
                        payment.getAmount().getCurrency()
                ))
                .toList();
    }

    @Override
    public List<MonthlyIncomeResponse> generateMonthlyIncomeReport(ReportFilter filter) {
        Map<java.time.YearMonth, List<Payment>> paymentsByMonth = filterPayments(filter).stream()
                .collect(Collectors.groupingBy(Payment::getPaymentMonth));

        return paymentsByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .map(entry -> {
                    BigDecimal totalIncome = entry.getValue().stream()
                            .map(payment -> payment.getAmount().getAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalLateFees = entry.getValue().stream()
                            .map(payment -> payment.getLateFee() == null
                                    ? BigDecimal.ZERO
                                    : payment.getLateFee().getAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    String currency = entry.getValue().isEmpty()
                            ? null
                            : entry.getValue().getFirst().getAmount().getCurrency();

                    return new MonthlyIncomeResponse(entry.getKey(), totalIncome, totalLateFees, currency);
                })
                .toList();
    }

    private List<Payment> filterPayments(ReportFilter filter) {
        return paymentRepository.findAll().stream()
                .filter(payment -> filter == null || matchesFilter(payment, filter))
                .toList();
    }

    private boolean matchesFilter(Payment payment, ReportFilter filter) {
        boolean matchesOwner = filter.ownerId() == null
                || Objects.equals(payment.getContract().getProperty().getOwner().getId(), filter.ownerId());
        boolean matchesProperty = filter.propertyId() == null
                || Objects.equals(payment.getContract().getProperty().getId(), filter.propertyId());
        boolean matchesTenant = filter.tenantId() == null
                || Objects.equals(payment.getContract().getTenant().getId(), filter.tenantId());
        boolean matchesStartDate = filter.startDate() == null || !payment.getPaymentDate().isBefore(filter.startDate());
        boolean matchesEndDate = filter.endDate() == null || !payment.getPaymentDate().isAfter(filter.endDate());
        boolean matchesMonth = filter.month() == null || Objects.equals(payment.getPaymentMonth(), filter.month());

        return matchesOwner && matchesProperty && matchesTenant && matchesStartDate && matchesEndDate && matchesMonth;
    }
}
