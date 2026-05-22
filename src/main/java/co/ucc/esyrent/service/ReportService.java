package co.ucc.esyrent.service;

import co.ucc.esyrent.dto.request.ReportFilter;
import co.ucc.esyrent.dto.response.MonthlyIncomeResponse;
import co.ucc.esyrent.dto.response.PaymentReportResponse;
import java.util.List;

public interface ReportService {

    List<PaymentReportResponse> generatePaymentReport(ReportFilter filter);

    List<MonthlyIncomeResponse> generateMonthlyIncomeReport(ReportFilter filter);
}
