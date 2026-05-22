package co.ucc.esyrent.controller;

import co.ucc.esyrent.dto.request.CreatePropertyRequest;
import co.ucc.esyrent.dto.request.UpdatePropertyRequest;
import co.ucc.esyrent.dto.response.PropertyResponse;
import co.ucc.esyrent.service.PropertyService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    @PreAuthorize("@securityAccessService.canCreateProperty(#request.ownerId(), authentication)")
    public ResponseEntity<PropertyResponse> createProperty(@Valid @RequestBody CreatePropertyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.createProperty(request));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PropertyResponse>> getProperties(@RequestParam(required = false) Long ownerId) {
        if (ownerId != null) {
            return ResponseEntity.ok(propertyService.getPropertiesByOwner(ownerId));
        }
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/{propertyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.getPropertyById(propertyId));
    }

    @PutMapping("/{propertyId}")
    @PreAuthorize("@securityAccessService.canManageProperty(#propertyId, authentication)")
    public ResponseEntity<PropertyResponse> updateProperty(@PathVariable Long propertyId,
                                                           @Valid @RequestBody UpdatePropertyRequest request) {
        return ResponseEntity.ok(propertyService.updateProperty(propertyId, request));
    }
}
