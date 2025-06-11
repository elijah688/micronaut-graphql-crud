package example.micronaut.security;

import example.micronaut.model.User;
import example.micronaut.repository.UserRepository;
import graphql.com.google.common.base.Optional;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.*;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import io.micronaut.security.token.render.BearerAccessRefreshToken;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/auth")
public class AuthenticationController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    @Inject
    AuthenticationProviderUserPassword authenticationProvider;

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    AuthenticationProviderUserPassword authenticationResponseHandler;

    @Inject
    JwtTokenGenerator jwtTokenGenerator;

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post("/signup")
    public HttpResponse<?> signup(@Body UsernamePasswordCredentials signupRequest) {

        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            return HttpResponse.badRequest("Username already exists");
        }
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRoles(Set.of("ROLE_USER"));
        userRepository.save(user);
        return HttpResponse.created(user);
    }

    @Post("/login")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<Map<String, String>> login(@Body UsernamePasswordCredentials credentials) {
        AuthenticationResponse authResponse = authenticationProvider.authenticate(null, credentials);
        if (authResponse.isAuthenticated()) {
            var authOpt = authResponse.getAuthentication();
            if (authOpt.isPresent()) {
                Authentication auth = authOpt.get();
                Map<String, Object> claims = new HashMap<>(auth.getAttributes());
                // Add roles claim explicitly
                LOG.info(auth.getRoles().toString());
                LOG.info(auth.getRoles().toString());
                LOG.info(auth.getRoles().toString());

                claims.put("roles", auth.getRoles());
               claims.put("sub", auth.getName());

                LOG.info(claims.toString());
                LOG.info(claims.toString());
                LOG.info(claims.toString());
                LOG.info(claims.toString());

                var tokenOpt = jwtTokenGenerator.generateToken(claims);
                if (tokenOpt.isPresent()) {
                    String token = tokenOpt.get();
                    Map<String, String> responseBody = Collections.singletonMap("access_token", token);
                    return HttpResponse.ok(responseBody);
                }
            }
            return HttpResponse.unauthorized();
        }
        return HttpResponse.unauthorized();
    }

}
