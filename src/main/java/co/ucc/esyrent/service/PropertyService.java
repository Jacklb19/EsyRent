package co.ucc.esyrent.service;

import co.ucc.esyrent.domain.enums.PropertyStatus;
import co.ucc.esyrent.dto.request.CreatePropertyRequest;
import co.ucc.esyrent.dto.request.UpdatePropertyRequest;
import co.ucc.esyrent.dto.response.PropertyResponse;
import java.util.List;

public interface PropertyService {

    PropertyResponse createProperty(CreatePropertyRequest request);

    PropertyResponse getPropertyById(Long propertyId);

    List<PropertyResponse> getAllProperties();

    List<PropertyResponse> getPropertiesByOwner(Long ownerId);

    List<PropertyResponse> getPropertiesByStatus(PropertyStatus status);

    List<PropertyResponse> getPropertiesByOwnerAndStatus(Long ownerId, PropertyStatus status);

    PropertyResponse updateProperty(Long propertyId, UpdatePropertyRequest request);
}
