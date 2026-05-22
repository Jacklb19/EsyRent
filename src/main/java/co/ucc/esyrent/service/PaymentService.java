package co.ucc.esyrent.service;

import co.ucc.esyrent.dto.request.RegisterPaymentRequest;
import co.ucc.esyrent.dto.response.PaymentResponse;
import java.util.List;

public interface PaymentService {

    PaymentResponse registerPayment(RegisterPaymentRequest request);

    List<PaymentResponse> getPaymentsByContract(Long contractId);
}
