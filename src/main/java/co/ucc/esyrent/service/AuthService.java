package co.ucc.esyrent.service;

import co.ucc.esyrent.dto.request.LoginRequest;
import co.ucc.esyrent.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);
}
