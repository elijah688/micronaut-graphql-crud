package example.micronaut;

import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorIntegrationTest extends GraphQLIntegrationTest {

    private static String authorId;

    @Test
    @Order(1)
    void createAuthor() {
        String query = "mutation { upsertAuthor(input: {firstName: \"John\", lastName: \"Doe\"}) { id firstName lastName } }";
        Map<String, Object> body = helper.makeRequest(query);
        Map<String, Object> author = helper.getData(body, "upsertAuthor");

        authorId = (String) author.get("id");
        assertNotNull(authorId);
        assertEquals("John", author.get("firstName"));
        assertEquals("Doe", author.get("lastName"));
    }

    @Test
    @Order(2)
    void getAuthor() {
        String query = String.format("query { authorById(id: \"%s\") { id firstName lastName } }", authorId);
        Map<String, Object> body = helper.makeRequest(query);
        Map<String, Object> author = helper.getData(body, "authorById");

        assertEquals(authorId, author.get("id"));
        assertEquals("John", author.get("firstName"));
        assertEquals("Doe", author.get("lastName"));
    }

    @Test
    @Order(3)
    void upsertAuthor() {
        String query = String.format(
                "mutation { upsertAuthor(input: {id: \"%s\", firstName: \"Johnny\", lastName: \"Doe\"}) { id firstName lastName } }",
                authorId);
        Map<String, Object> body = helper.makeRequest(query);
        Map<String, Object> author = helper.getData(body, "upsertAuthor");

        assertEquals(authorId, author.get("id"));
        assertEquals("Johnny", author.get("firstName"));
    }
}
