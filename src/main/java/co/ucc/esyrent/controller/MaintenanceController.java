package co.ucc.esyrent.controller;

import co.ucc.esyrent.dto.request.CreateMaintenanceRequest;
import co.ucc.esyrent.dto.response.MaintenanceResponse;
import co.ucc.esyrent.service.MaintenanceService;
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
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping
    public ResponseEntity<MaintenanceResponse> createMaintenanceRequest(
            @Valid @RequestBody CreateMaintenanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceService.createMaintenanceRequest(request));
    }

    @PutMapping("/{maintenanceRequestId}/advance")
    public ResponseEntity<MaintenanceResponse> advanceStatus(@PathVariable Long maintenanceRequestId) {
        return ResponseEntity.ok(maintenanceService.advanceStatus(maintenanceRequestId));
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceResponse>> getByContract(@RequestParam Long contractId) {
        return ResponseEntity.ok(maintenanceService.getByContract(contractId));
    }
}
