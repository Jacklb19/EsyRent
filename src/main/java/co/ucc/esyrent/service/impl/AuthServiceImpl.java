package co.ucc.esyrent.service.impl;

import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.dto.request.LoginRequest;
import co.ucc.esyrent.dto.request.RefreshTokenRequest;
import co.ucc.esyrent.dto.response.AuthResponse;
import co.ucc.esyrent.exception.BusinessRuleException;
import co.ucc.esyrent.exception.ResourceNotFoundException;
import co.ucc.esyrent.mapper.UserMapper;
import co.ucc.esyrent.repository.RefreshTokenRepository;
import co.ucc.esyrent.repository.UserRepository;
import co.ucc.esyrent.security.JwtProvider;
import co.ucc.esyrent.security.UserDetailsServiceImpl;
import co.ucc.esyrent.service.AuthService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserMapper userMapper;
    private final long refreshExpirationMillis;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtProvider jwtProvider,
                           UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                           UserDetailsServiceImpl userDetailsService, UserMapper userMapper,
                           @org.springframework.beans.factory.annotation.Value("${jwt.refresh-expiration}") long refreshExpirationMillis) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with email " + request.email() + " was not found"));

        return buildAuthResponse(user, jwtProvider.generateToken(authentication));
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        co.ucc.esyrent.domain.entity.RefreshToken storedToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BusinessRuleException("Refresh token is invalid"));

        if (!storedToken.isActive()) {
            storedToken.revoke();
            throw new BusinessRuleException("Refresh token is expired or revoked");
        }

        storedToken.revoke();
        User user = storedToken.getUser();
        org.springframework.security.core.userdetails.UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());
        return buildAuthResponse(user, jwtProvider.generateToken(userDetails));
    }

    private AuthResponse buildAuthResponse(User user, String accessToken) {
        refreshTokenRepository.deleteByUser(user);
        String refreshTokenValue = generateRefreshTokenValue();
        refreshTokenRepository.save(new co.ucc.esyrent.domain.entity.RefreshToken(
                user,
                refreshTokenValue,
                LocalDateTime.now().plusNanos(refreshExpirationMillis * 1_000_000L)
        ));
        return new AuthResponse(accessToken, refreshTokenValue, userMapper.toResponse(user));
    }

    private String generateRefreshTokenValue() {
        byte[] bytes = new byte[64];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
