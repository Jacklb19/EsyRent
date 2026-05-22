package co.ucc.esyrent.mapper;

import co.ucc.esyrent.domain.entity.MaintenanceRequest;
import co.ucc.esyrent.dto.response.MaintenanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MaintenanceMapper {

    @Mapping(target = "contractId", source = "contract.id")
    MaintenanceResponse toResponse(MaintenanceRequest request);
}
