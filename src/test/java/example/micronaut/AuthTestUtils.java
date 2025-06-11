package example.micronaut;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpStatus;

import java.util.Map;

public class AuthTestUtils {

        public static String getJwtToken(HttpClient httpClient, String username, String password) throws Exception {

                // Signup request
                Map<String, String> signupBody = Map.of(
                                "username", username,
                                "password", password);

                HttpRequest<?> signupRequest = HttpRequest.POST("/auth/signup", signupBody)
                                .contentType(MediaType.APPLICATION_JSON);

                HttpResponse<?> signupResponse = httpClient.toBlocking().exchange(signupRequest);
                if (!(signupResponse.getStatus().equals(HttpStatus.CREATED) ||
                                signupResponse.getStatus().equals(HttpStatus.OK) ||
                                signupResponse.getStatus().equals(HttpStatus.CONFLICT))) {
                        throw new RuntimeException("Signup failed with status " + signupResponse.getStatus());
                }

                // Login request
                Map<String, String> loginBody = Map.of(
                                "username", username,
                                "password", password);

                HttpRequest<?> loginRequest = HttpRequest.POST("/auth/login", loginBody)
                                .contentType(MediaType.APPLICATION_JSON);

                HttpResponse<Map<String, Object>> loginResponse = httpClient.toBlocking().exchange(loginRequest,
                                Argument.mapOf(String.class, Object.class));

                if (loginResponse.getStatus() != HttpStatus.OK) {
                        throw new RuntimeException("Login failed with status " + loginResponse.getStatus());
                }

                Map<String, Object> body = loginResponse.body();
                String jwtToken = (String) body.get("access_token");

                if (jwtToken == null || jwtToken.isEmpty()) {
                        throw new RuntimeException("Token not found in login response");
                }

                return jwtToken;
        }
}
