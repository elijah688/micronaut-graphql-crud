package example.micronaut.controller;

import example.micronaut.model.User;
import example.micronaut.repository.UserRepository;
import io.micronaut.http.annotation.*;
import io.micronaut.security.authentication.PasswordEncoder;
import jakarta.inject.Inject;

import java.util.Set;

@Controller("/auth")
public class AuthController {

    @Inject
    private UserRepository userRepository;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Post("/signup")
    public User signup(@Body SignupRequest signupRequest) {
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRoles(Set.of("ROLE_USER")); // default role

        return userRepository.save(user);
    }

    public static class SignupRequest {
        private String username;
        private String password;

        // getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
