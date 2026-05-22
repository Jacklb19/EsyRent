package co.ucc.esyrent.dto.response;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
