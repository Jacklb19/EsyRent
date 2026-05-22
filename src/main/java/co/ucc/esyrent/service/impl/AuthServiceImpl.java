package co.ucc.esyrent.service.impl;

import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.dto.request.LoginRequest;
import co.ucc.esyrent.dto.response.AuthResponse;
import co.ucc.esyrent.exception.ResourceNotFoundException;
import co.ucc.esyrent.mapper.UserMapper;
import co.ucc.esyrent.repository.UserRepository;
import co.ucc.esyrent.security.JwtProvider;
import co.ucc.esyrent.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtProvider jwtProvider,
                           UserRepository userRepository, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with email " + request.email() + " was not found"));

        return new AuthResponse(jwtProvider.generateToken(authentication), userMapper.toResponse(user));
    }
}
