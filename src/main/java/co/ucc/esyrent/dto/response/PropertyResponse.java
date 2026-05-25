package co.ucc.esyrent.dto.response;

import co.ucc.esyrent.domain.enums.PropertyStatus;
import co.ucc.esyrent.domain.enums.PropertyType;
import java.math.BigDecimal;
import java.util.List;

public record PropertyResponse(
        Long id,
        Long ownerId,
        String ownerName,
        String address,
        PropertyType type,
        BigDecimal areaM2,
        BigDecimal referenceRentAmount,
        String referenceRentCurrency,
        String description,
        PropertyStatus status,
        List<FileResponse> attachments
) {
}
