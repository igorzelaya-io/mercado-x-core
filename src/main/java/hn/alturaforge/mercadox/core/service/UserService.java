package hn.alturaforge.mercadox.core.service;

import hn.alturaforge.mercadox.library.entity.model.auth.User;
import hn.alturaforge.mercadox.library.jpa.repository.UserRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllEnabledOrgAdmins() {
        return userRepository.findAllEnabledOrgAdmins();
    }

    public List<User> findAvailableDrivers() {
        return userRepository.findAvailableDrivers();
    }

    public User findActiveUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("User not found for username: '%s'", username)));
    }

    public User findActiveUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User was not found for userId: '%s'", userId));
    }

    public User findActiveUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found for: '%s'", email));
    }

    public boolean existsById(String orgId) {
        return userRepository.existsById(UUID.fromString(orgId));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

}
