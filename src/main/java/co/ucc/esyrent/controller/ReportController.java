package co.ucc.esyrent.controller;

import co.ucc.esyrent.dto.request.ReportFilter;
import co.ucc.esyrent.dto.response.MonthlyIncomeResponse;
import co.ucc.esyrent.dto.response.PaymentReportResponse;
import co.ucc.esyrent.service.ReportService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<List<PaymentReportResponse>> generatePaymentReport(@ModelAttribute ReportFilter filter) {
        return ResponseEntity.ok(reportService.generatePaymentReport(filter));
    }

    @GetMapping("/monthly-income")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<List<MonthlyIncomeResponse>> generateMonthlyIncomeReport(
            @ModelAttribute ReportFilter filter) {
        return ResponseEntity.ok(reportService.generateMonthlyIncomeReport(filter));
    }
}
