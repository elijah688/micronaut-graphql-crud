package example.micronaut;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(application = Application.class, packages = "example.micronaut")
public class AuthorGraphQLIntegrationTest {

    @Inject
    @Client("/graphql")
    HttpClient client;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    DbInitializer dbInitializer;

    @Test
    void insertAuthors() {
        assertNotNull(dbInitializer);

        List<String> mutations = List.of(
                "mutation { upsertAuthor(input: {firstName: \"Alice\", lastName: \"Smith\"}) { id firstName lastName } }",
                "mutation { upsertAuthor(input: {firstName: \"Bob\",   lastName: \"Johnson\"}) { id firstName lastName } }",
                "mutation { upsertAuthor(input: {firstName: \"Carol\", lastName: \"Williams\"}) { id firstName lastName } }");
        List<String> expectedFirstNames = List.of("Alice", "Bob", "Carol");
        List<String> expectedLastNames = List.of("Smith", "Johnson", "Williams");

        for (int i = 0; i < mutations.size(); i++) {
            String mutation = mutations.get(i);

            Map<String, Object> body = makeRequest(mutation);
            assertNotNull(body, "Response body should not be null");
            assertTrue(body.containsKey("data"), "Response should contain 'data'");

            Map<String, Object> data = (Map<String, Object>) body.get("data");
            assertTrue(data.containsKey("upsertAuthor"), "Data should contain 'upsertAuthor'");

            Map<String, Object> author = (Map<String, Object>) data.get("upsertAuthor");
            assertNotNull(author.get("id"), "Author ID should not be null");
            assertEquals(expectedFirstNames.get(i), author.get("firstName"));
            assertEquals(expectedLastNames.get(i), author.get("lastName"));
        }
    }

    private Map<String, Object> makeRequest(String query) {
        try {
            // Safely build JSON
            String json = objectMapper.writeValueAsString(Map.of("query", query));
            HttpRequest<String> request = HttpRequest.POST("/", json);
            HttpResponse<Map<String, Object>> response = client.toBlocking().exchange(
                    request, Argument.mapOf(String.class, Object.class));
            assertEquals(HttpStatus.OK, response.getStatus());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("GraphQL request failed", e);
        }
    }
}
