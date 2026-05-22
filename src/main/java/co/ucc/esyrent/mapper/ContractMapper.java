package co.ucc.esyrent.mapper;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.dto.response.ContractResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "tenantId", source = "tenant.id")
    @Mapping(target = "tenantName", source = "tenant.fullName")
    @Mapping(target = "monthlyRentAmount", source = "monthlyRent.amount")
    @Mapping(target = "monthlyRentCurrency", source = "monthlyRent.currency")
    @Mapping(target = "cutoffDay", source = "cutoff.day")
    @Mapping(target = "depositAmount", source = "deposit.amount")
    @Mapping(target = "depositCurrency", source = "deposit.currency")
    @Mapping(target = "cancellationReason", source = "cancellation.reason")
    @Mapping(target = "cancellationDate", source = "cancellation.date")
    ContractResponse toResponse(Contract contract);
}
