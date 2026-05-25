package co.ucc.esyrent.mapper;

import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.dto.response.PropertyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {FileMapper.class})
public interface PropertyMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", source = "owner.fullName")
    @Mapping(target = "referenceRentAmount", source = "referenceRent.amount")
    @Mapping(target = "referenceRentCurrency", source = "referenceRent.currency")
    PropertyResponse toResponse(Property property);
}
