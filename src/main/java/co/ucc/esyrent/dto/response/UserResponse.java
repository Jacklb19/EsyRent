package co.ucc.esyrent.dto.response;

import co.ucc.esyrent.domain.enums.UserRole;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        UserRole role
) {
}
