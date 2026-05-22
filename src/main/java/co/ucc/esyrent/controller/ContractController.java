package co.ucc.esyrent.controller;

import co.ucc.esyrent.dto.request.CancelContractRequest;
import co.ucc.esyrent.dto.request.CreateContractRequest;
import co.ucc.esyrent.dto.response.ContractResponse;
import co.ucc.esyrent.service.ContractService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping
    public ResponseEntity<ContractResponse> createContract(@Valid @RequestBody CreateContractRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contractService.createContract(request));
    }

    @GetMapping("/{contractId}")
    public ResponseEntity<ContractResponse> getContractById(@PathVariable Long contractId) {
        return ResponseEntity.ok(contractService.getContractById(contractId));
    }

    @GetMapping
    public ResponseEntity<List<ContractResponse>> getContracts(@RequestParam(required = false) Long tenantId,
                                                               @RequestParam(required = false) Long propertyId) {
        if (tenantId != null) {
            return ResponseEntity.ok(contractService.getContractsByTenant(tenantId));
        }
        if (propertyId != null) {
            return ResponseEntity.ok(contractService.getContractsByProperty(propertyId));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{contractId}/cancel")
    public ResponseEntity<ContractResponse> cancelContract(@PathVariable Long contractId,
                                                           @Valid @RequestBody CancelContractRequest request) {
        return ResponseEntity.ok(contractService.cancelContract(contractId, request));
    }
}
