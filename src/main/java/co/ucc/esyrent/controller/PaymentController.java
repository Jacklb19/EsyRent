package co.ucc.esyrent.controller;

import co.ucc.esyrent.dto.request.RegisterPaymentRequest;
import co.ucc.esyrent.dto.response.PaymentResponse;
import co.ucc.esyrent.service.PaymentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("@securityAccessService.canRegisterPayment(#request.contractId(), authentication)")
    public ResponseEntity<PaymentResponse> registerPayment(@Valid @RequestBody RegisterPaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.registerPayment(request));
    }

    @GetMapping
    @PreAuthorize("@securityAccessService.canAccessPaymentsByContract(#contractId, authentication)")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByContract(@RequestParam Long contractId) {
        return ResponseEntity.ok(paymentService.getPaymentsByContract(contractId));
    }
}
