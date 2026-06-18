package ch.chattrix.userservice.service;

import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.userservice.entity.User;
import ch.chattrix.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ApiResponse<Void> create(String username, UUID userUuid) {

        if (userRepository.existsById(userUuid)) {
            return new ApiResponse<>(false, "USER_ALREADY_EXISTS", null);
        }

        if (userRepository.existsByUsername(username)) {
            return new ApiResponse<>(false, "USERNAME_ALREADY_EXISTS", null);
        }

        try {
            User user = new User();
            user.setUserUuid(userUuid);
            user.setUsername(username);
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());

            userRepository.save(user);

            return new ApiResponse<>(true, "USER_CREATED_SUCCESSFULLY", null);

        } catch (Exception e) {
            return new ApiResponse<>(false, "USER_CREATION_FAILED", null);
        }
    }
}