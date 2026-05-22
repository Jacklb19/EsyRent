package co.ucc.esyrent.service.impl;

import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.dto.request.CreateUserRequest;
import co.ucc.esyrent.dto.request.UpdateProfileRequest;
import co.ucc.esyrent.dto.response.UserResponse;
import co.ucc.esyrent.exception.BusinessRuleException;
import co.ucc.esyrent.exception.ResourceNotFoundException;
import co.ucc.esyrent.mapper.UserMapper;
import co.ucc.esyrent.repository.UserRepository;
import co.ucc.esyrent.service.UserService;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("A user with email " + request.email() + " already exists");
        }

        User user = new User(
                request.fullName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.phone(),
                request.role()
        );
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(Long userId) {
        return userMapper.toResponse(findUserById(userId));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUserById(userId);
        user.updateProfile(request.fullName(), request.phone());
        return userMapper.toResponse(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " was not found"));
    }
}
