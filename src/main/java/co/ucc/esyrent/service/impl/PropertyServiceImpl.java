package co.ucc.esyrent.service.impl;

import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.domain.enums.PropertyStatus;
import co.ucc.esyrent.domain.valueobject.MoneyAmount;
import co.ucc.esyrent.dto.request.CreatePropertyRequest;
import co.ucc.esyrent.dto.request.UpdatePropertyRequest;
import co.ucc.esyrent.dto.response.PropertyResponse;
import co.ucc.esyrent.exception.ResourceNotFoundException;
import co.ucc.esyrent.mapper.PropertyMapper;
import co.ucc.esyrent.repository.PropertyRepository;
import co.ucc.esyrent.repository.UserRepository;
import co.ucc.esyrent.service.PropertyService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyMapper propertyMapper;

    public PropertyServiceImpl(PropertyRepository propertyRepository, UserRepository userRepository,
                               PropertyMapper propertyMapper) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.propertyMapper = propertyMapper;
    }

    @Override
    @Transactional
    public PropertyResponse createProperty(CreatePropertyRequest request) {
        User owner = findUserById(request.ownerId());
        Property property = new Property(
                owner,
                request.address(),
                request.type(),
                request.areaM2(),
                new MoneyAmount(request.referenceRentAmount(), request.referenceRentCurrency()).normalizeScale(),
                request.description()
        );
        return propertyMapper.toResponse(propertyRepository.save(property));
    }

    @Override
    public PropertyResponse getPropertyById(Long propertyId) {
        return propertyMapper.toResponse(findPropertyById(propertyId));
    }

    @Override
    public List<PropertyResponse> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(propertyMapper::toResponse)
                .toList();
    }

    @Override
    public List<PropertyResponse> getPropertiesByOwner(Long ownerId) {
        User owner = findUserById(ownerId);
        return propertyRepository.findByOwner(owner).stream()
                .map(propertyMapper::toResponse)
                .toList();
    }

    @Override
    public List<PropertyResponse> getPropertiesByStatus(PropertyStatus status) {
        return propertyRepository.findByStatus(status).stream()
                .map(propertyMapper::toResponse)
                .toList();
    }

    @Override
    public List<PropertyResponse> getPropertiesByOwnerAndStatus(Long ownerId, PropertyStatus status) {
        User owner = findUserById(ownerId);
        return propertyRepository.findByOwnerAndStatus(owner, status).stream()
                .map(propertyMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PropertyResponse updateProperty(Long propertyId, UpdatePropertyRequest request) {
        Property property = findPropertyById(propertyId);
        property.updateDetails(
                request.address(),
                request.areaM2(),
                new MoneyAmount(request.referenceRentAmount(), request.referenceRentCurrency()).normalizeScale(),
                request.description()
        );
        return propertyMapper.toResponse(property);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " was not found"));
    }

    private Property findPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property with id " + propertyId + " was not found"));
    }
}
