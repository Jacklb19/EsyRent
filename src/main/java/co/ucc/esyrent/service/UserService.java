package co.ucc.esyrent.service;

import co.ucc.esyrent.dto.request.CreateUserRequest;
import co.ucc.esyrent.dto.request.UpdateProfileRequest;
import co.ucc.esyrent.dto.response.UserResponse;
import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long userId);

    List<UserResponse> getAllUsers();

    UserResponse updateProfile(Long userId, UpdateProfileRequest request);
}
