package co.ucc.esyrent.mapper;

import co.ucc.esyrent.domain.entity.RentalApplication;
import co.ucc.esyrent.dto.response.RentalApplicationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RentalApplicationMapper {

    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "propertyAddress", source = "property.address")
    @Mapping(target = "ownerId", source = "property.owner.id")
    @Mapping(target = "ownerName", source = "property.owner.fullName")
    @Mapping(target = "tenantId", source = "tenant.id")
    @Mapping(target = "tenantName", source = "tenant.fullName")
    @Mapping(target = "contractId", source = "contract.id")
    RentalApplicationResponse toResponse(RentalApplication application);
}
