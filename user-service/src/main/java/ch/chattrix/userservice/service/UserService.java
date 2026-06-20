package ch.chattrix.userservice.service;

import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.UserAnonymData;
import ch.chattrix.shared.types.UserBaseData;
import ch.chattrix.userservice.entity.User;
import ch.chattrix.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;
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
            user.setUsername(username.trim());
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());

            userRepository.save(user);

            return new ApiResponse<>(true, "USER_CREATED_SUCCESSFULLY", null);

        } catch (Exception e) {
            return new ApiResponse<>(false, "USER_CREATION_FAILED", null);
        }
    }

    public ApiResponse<List<UserAnonymData>> getAll() {
        try {
            List<UserAnonymData> result = userRepository.findAll()
                    .stream()
                    .map(user -> new UserAnonymData(user.getUsername(), user.getUserUuid()))
                    .toList();

            return new ApiResponse<>(true, "USER_GET_ALL_SUCCESSFULLY", result);

        } catch (Exception e) {
            return new ApiResponse<>(false, "USER_GET_ALL_FAILED", null);
        }
    }

    public ApiResponse<UserBaseData> getOne(UUID userUuid) {

        try {
            return userRepository.findByUserUuid(userUuid)
                    .map(user -> {
                        UserBaseData data = new UserBaseData();
                        data.setUserUuid(user.getUserUuid());
                        data.setUsername(user.getUsername());
                        data.setCreatedAt(user.getCreatedAt());

                        return new ApiResponse<>(true, "USER_GET_ONE_SUCCESS", data);
                    })
                    .orElseGet(() -> new ApiResponse<>(false, "USER_NOT_FOUND", null));

        } catch (Exception e) {
            return new ApiResponse<>(false, "USER_GET_ONE_FAILED", null);
        }
    }

    @Transactional
    public ApiResponse<Void> editUsername(UUID userUuid, String username) {

        if (username == null || username.isBlank()) {
            return new ApiResponse<>(false, "NO_USERNAME_PROVIDED", null);
        }

        username = username.trim();

        User user = userRepository.findByUserUuid(userUuid)
                .orElse(null);

        if (user == null) {
            return new ApiResponse<>(false, "USER_NOT_FOUND", null);
        }

        if (userRepository.existsByUsernameAndUserUuidNot(username, userUuid)) {
            return new ApiResponse<>(false, "USERNAME_ALREADY_EXISTS", null);
        }

        user.setUsername(username);
        user.setUpdatedAt(Date.from(Instant.now()));

        return new ApiResponse<>(true, "USER_EDIT_USERNAME_SUCCESS", null);
    }

    @Transactional
    public ApiResponse<Void> delete(UUID userUuid) {

        try {
            User user = userRepository.findByUserUuid(userUuid)
                    .orElse(null);

            if (user == null) {
                return new ApiResponse<>(false, "USER_NOT_FOUND", null);
            }

            userRepository.delete(user);

            return new ApiResponse<>(true, "USER_DELETE_SUCCESS", null);

        } catch (Exception e) {
            return new ApiResponse<>(false, "USER_DELETE_FAILED", null);
        }
    }
}