package ch.chattrix.userservice.service;

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

    public boolean create(String username, UUID userUuid) {

        if (userRepository.existsById(userUuid)) {
            return true;
        }

        if(userRepository.existsByUsername(username)){
            return true;
        }

        User user = new User();
        user.setUserUuid(userUuid);
        user.setUsername(username);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        userRepository.save(user);

        return true;
    }
}
