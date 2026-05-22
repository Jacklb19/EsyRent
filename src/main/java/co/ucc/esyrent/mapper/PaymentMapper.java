package co.ucc.esyrent.mapper;

import co.ucc.esyrent.domain.entity.Payment;
import co.ucc.esyrent.dto.response.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "contractId", source = "contract.id")
    @Mapping(target = "amount", source = "amount.amount")
    @Mapping(target = "currency", source = "amount.currency")
    @Mapping(target = "lateFeeAmount", source = "lateFee.amount")
    @Mapping(target = "lateFeeCurrency", source = "lateFee.currency")
    PaymentResponse toResponse(Payment payment);
}
