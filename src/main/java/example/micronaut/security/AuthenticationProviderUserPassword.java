package example.micronaut.security;

import example.micronaut.model.User;
import example.micronaut.repository.UserRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

@Singleton
public class AuthenticationProviderUserPassword implements HttpRequestAuthenticationProvider<Object> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationProviderUserPassword(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticationResponse authenticate(@Nullable HttpRequest<Object> httpRequest,
                                               @NonNull AuthenticationRequest<String, String> authenticationRequest) {
        String username = authenticationRequest.getIdentity();
        String password = authenticationRequest.getSecret();

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();
            return AuthenticationResponse.success(username, List.copyOf(user.getRoles()));
        } else {
            return AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
        }
    }
}
