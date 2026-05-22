package co.ucc.esyrent.security;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.MaintenanceRequest;
import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.dto.request.ReportFilter;
import co.ucc.esyrent.dto.request.UploadFileRequest;
import co.ucc.esyrent.repository.ContractRepository;
import co.ucc.esyrent.repository.MaintenanceRequestRepository;
import co.ucc.esyrent.repository.PropertyRepository;
import co.ucc.esyrent.repository.UserRepository;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("securityAccessService")
public class SecurityAccessService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final ContractRepository contractRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public SecurityAccessService(UserRepository userRepository, PropertyRepository propertyRepository,
                                 ContractRepository contractRepository,
                                 MaintenanceRequestRepository maintenanceRequestRepository) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.contractRepository = contractRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    public boolean canAccessUser(Long userId, Authentication authentication) {
        return isAdmin(authentication) || isSameUser(userId, authentication);
    }

    public boolean canCreateProperty(Long ownerId, Authentication authentication) {
        return isAdmin(authentication) || isSameUser(ownerId, authentication);
    }

    public boolean canManageProperty(Long propertyId, Authentication authentication) {
        return isAdmin(authentication) || propertyRepository.findById(propertyId)
                .map(property -> isOwnerOfProperty(property, authentication))
                .orElse(false);
    }

    public boolean canManageContractCreation(Long propertyId, Authentication authentication) {
        return canManageProperty(propertyId, authentication);
    }

    public boolean canAccessContract(Long contractId, Authentication authentication) {
        return isAdmin(authentication) || contractRepository.findById(contractId)
                .map(contract -> isOwnerOfProperty(contract.getProperty(), authentication)
                        || isTenantOfContract(contract, authentication))
                .orElse(false);
    }

    public boolean canManageContract(Long contractId, Authentication authentication) {
        return isAdmin(authentication) || contractRepository.findById(contractId)
                .map(contract -> isOwnerOfProperty(contract.getProperty(), authentication))
                .orElse(false);
    }

    public boolean canQueryContracts(Long tenantId, Long propertyId, Authentication authentication) {
        if (isAdmin(authentication)) {
            return true;
        }
        if (tenantId != null) {
            return isSameUser(tenantId, authentication);
        }
        if (propertyId != null) {
            return canManageProperty(propertyId, authentication);
        }
        return false;
    }

    public boolean canRegisterPayment(Long contractId, Authentication authentication) {
        return isAdmin(authentication) || contractRepository.findById(contractId)
                .map(contract -> isTenantOfContract(contract, authentication))
                .orElse(false);
    }

    public boolean canAccessPaymentsByContract(Long contractId, Authentication authentication) {
        return canAccessContract(contractId, authentication);
    }

    public boolean canCreateMaintenance(Long contractId, Authentication authentication) {
        return isAdmin(authentication) || contractRepository.findById(contractId)
                .map(contract -> isOwnerOfProperty(contract.getProperty(), authentication)
                        || isTenantOfContract(contract, authentication))
                .orElse(false);
    }

    public boolean canAdvanceMaintenance(Long maintenanceRequestId, Authentication authentication) {
        return isAdmin(authentication) || maintenanceRequestRepository.findById(maintenanceRequestId)
                .map(request -> isOwnerOfProperty(request.getContract().getProperty(), authentication))
                .orElse(false);
    }

    public boolean canAccessMaintenanceByContract(Long contractId, Authentication authentication) {
        return canAccessContract(contractId, authentication);
    }

    public boolean canUploadFile(UploadFileRequest request, Authentication authentication) {
        if (request == null || request.type() == null) {
            return false;
        }
        return switch (request.type()) {
            case PROPERTY_IMAGE -> request.propertyId() != null && canManageProperty(request.propertyId(), authentication);
            case CONTRACT_PDF -> request.contractId() != null && canManageContract(request.contractId(), authentication);
            case MAINTENANCE_EVIDENCE -> request.maintenanceRequestId() != null
                    && canAccessMaintenanceRequest(request.maintenanceRequestId(), authentication);
        };
    }

    public boolean canAccessFilesForProperty(Long propertyId, Authentication authentication) {
        return canManageProperty(propertyId, authentication);
    }

    public boolean canAccessFilesForContract(Long contractId, Authentication authentication) {
        return canAccessContract(contractId, authentication);
    }

    public boolean canAccessFilesForMaintenance(Long maintenanceRequestId, Authentication authentication) {
        return canAccessMaintenanceRequest(maintenanceRequestId, authentication);
    }

    public boolean canAccessPaymentReport(ReportFilter filter, Authentication authentication) {
        return canAccessReport(filter, authentication);
    }

    public boolean canAccessMonthlyIncomeReport(ReportFilter filter, Authentication authentication) {
        return canAccessReport(filter, authentication);
    }

    private boolean canAccessReport(ReportFilter filter, Authentication authentication) {
        if (isAdmin(authentication)) {
            return true;
        }
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
        if (currentUser.isEmpty() || !currentUser.get().isOwner()) {
            return false;
        }

        Long currentUserId = currentUser.get().getId();
        if (filter == null) {
            return false;
        }
        if (filter.ownerId() != null) {
            return currentUserId.equals(filter.ownerId());
        }
        if (filter.propertyId() != null) {
            return propertyRepository.findById(filter.propertyId())
                    .map(property -> property.getOwner().getId().equals(currentUserId))
                    .orElse(false);
        }
        return false;
    }

    private boolean canAccessMaintenanceRequest(Long maintenanceRequestId, Authentication authentication) {
        return isAdmin(authentication) || maintenanceRequestRepository.findById(maintenanceRequestId)
                .map(request -> isOwnerOfProperty(request.getContract().getProperty(), authentication)
                        || isTenantOfContract(request.getContract(), authentication))
                .orElse(false);
    }

    private boolean isSameUser(Long userId, Authentication authentication) {
        if (userId == null || authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return userRepository.findById(userId)
                .map(user -> user.getEmail().equalsIgnoreCase(authentication.getName()))
                .orElse(false);
    }

    private boolean isOwnerOfProperty(Property property, Authentication authentication) {
        return property != null
                && authentication != null
                && authentication.isAuthenticated()
                && property.getOwner().getEmail().equalsIgnoreCase(authentication.getName());
    }

    private boolean isTenantOfContract(Contract contract, Authentication authentication) {
        return contract != null
                && authentication != null
                && authentication.isAuthenticated()
                && contract.getTenant().getEmail().equalsIgnoreCase(authentication.getName());
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
