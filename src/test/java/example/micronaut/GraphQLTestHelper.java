package example.micronaut;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.type.Argument;
import io.micronaut.http.*;
import io.micronaut.http.client.HttpClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GraphQLTestHelper {

    private final HttpClient graphqlClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String jwtToken;

    public GraphQLTestHelper(HttpClient graphqlClient, String jwtToken) {
        this.graphqlClient = graphqlClient;
        this.jwtToken = jwtToken;
    }

    public Map<String, Object> makeRequest(String query) {
        try {
            String json = objectMapper.writeValueAsString(Map.of("query", query));
            System.out.println("Sending GraphQL query JSON: " + json);

            HttpRequest<String> request = HttpRequest.POST("/", json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bearerAuth(jwtToken);

            HttpResponse<Map<String, Object>> response = graphqlClient.toBlocking().exchange(
                    request,
                    Argument.mapOf(String.class, Object.class));

            System.out.println("Received response status: " + response.getStatus());
            System.out.println("Response body: " + response.body());

            assertEquals(HttpStatus.OK, response.getStatus());
            return response.body();
        } catch (io.micronaut.http.client.exceptions.HttpClientResponseException e) {
            System.err.println("GraphQL request failed with status: " + e.getStatus());
            e.getResponse().getBody(String.class)
                    .ifPresent(body -> System.err.println("Response body: " + body));
            throw new RuntimeException("GraphQL request failed", e);
        } catch (Exception e) {
            throw new RuntimeException("GraphQL request failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getData(Map<String, Object> body, String key) {
        assertNotNull(body, "Response body is null");
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertNotNull(data, "Response 'data' field is null");
        Object val = data.get(key);
        assertNotNull(val, "Response data missing key: " + key);
        return (Map<String, Object>) val;
    }

    @SuppressWarnings("unchecked")
    public String getBookName(Map<String, Object> edge) {
        Map<String, Object> node = (Map<String, Object>) edge.get("node");
        return (String) node.get("name");
    }
}
