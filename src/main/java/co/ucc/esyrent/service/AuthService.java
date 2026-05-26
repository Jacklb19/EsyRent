package co.ucc.esyrent.service;

import co.ucc.esyrent.dto.request.LoginRequest;
import co.ucc.esyrent.dto.request.RefreshTokenRequest;
import co.ucc.esyrent.dto.response.AuthResponse;

import co.ucc.esyrent.dto.request.ForgotPasswordRequest;
import co.ucc.esyrent.dto.request.ResetPasswordRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
