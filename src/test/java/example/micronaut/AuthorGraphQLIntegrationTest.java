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
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(application = Application.class, packages = "example.micronaut")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorGraphQLIntegrationTest {

    @Inject
    @Client("/graphql")
    HttpClient client;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    DbInitializer dbInitializer;

    private static String authorId;
    private static String bookId;

    @Test
    @Order(1)
    void createAuthor() {
        String query = "mutation { upsertAuthor(input: {firstName: \"John\", lastName: \"Doe\"}) { id firstName lastName } }";
        Map<String, Object> body = makeRequest(query);
        Map<String, Object> author = getData(body, "upsertAuthor");

        authorId = (String) author.get("id");
        assertNotNull(authorId);
        assertEquals("John", author.get("firstName"));
        assertEquals("Doe", author.get("lastName"));
    }

    @Test
    @Order(2)
    void getAuthor() {
        String query = String.format("query { authorById(id: \"%s\") { id firstName lastName } }", authorId);
        Map<String, Object> body = makeRequest(query);
        Map<String, Object> author = getData(body, "authorById");

        assertEquals(authorId, author.get("id"));
        assertEquals("John", author.get("firstName"));
        assertEquals("Doe", author.get("lastName"));
    }

    @Test
    @Order(3)
    void upsertAuthor() {
        String query = String.format("mutation { upsertAuthor(input: {id: \"%s\", firstName: \"Johnny\", lastName: \"Doe\"}) { id firstName lastName } }", authorId);
        Map<String, Object> body = makeRequest(query);
        Map<String, Object> author = getData(body, "upsertAuthor");

        assertEquals(authorId, author.get("id"));
        assertEquals("Johnny", author.get("firstName"));
    }

    @Test
    @Order(4)
    void createBook() {
        String query = String.format("mutation { upsertBook(input: {name: \"GraphQL 101\", pageCount: 150, authorId: \"%s\"}) { id name pageCount author { id } } }", authorId);
        Map<String, Object> body = makeRequest(query);
        Map<String, Object> book = getData(body, "upsertBook");

        bookId = (String) book.get("id");
        assertEquals("GraphQL 101", book.get("name"));
        assertEquals(150, book.get("pageCount"));
        assertEquals(authorId, ((Map<String, Object>) book.get("author")).get("id"));
    }

    @Test
    @Order(5)
    void getBook() {
        String query = String.format("query { bookById(id: \"%s\") { id name pageCount author { id } } }", bookId);
        Map<String, Object> body = makeRequest(query);
        Map<String, Object> book = getData(body, "bookById");

        assertEquals(bookId, book.get("id"));
        assertEquals("GraphQL 101", book.get("name"));
        assertEquals(150, book.get("pageCount"));
    }

    @Test
    @Order(6)
    void upsertBook() {
        String query = String.format("mutation { upsertBook(input: {id: \"%s\", name: \"GraphQL Advanced\", pageCount: 300, authorId: \"%s\"}) { id name pageCount } }", bookId, authorId);
        Map<String, Object> body = makeRequest(query);
        Map<String, Object> book = getData(body, "upsertBook");

        assertEquals(bookId, book.get("id"));
        assertEquals("GraphQL Advanced", book.get("name"));
        assertEquals(300, book.get("pageCount"));
    }

    @Test
    @Order(7)
    void paginationTest() {
        for (int i = 1; i <= 25; i++) {
            String name = "Book " + i;
            String mutation = String.format("mutation { upsertBook(input: {name: \"%s\", pageCount: %d, authorId: \"%s\"}) { id } }", name, i * 10, authorId);
            makeRequest(mutation);
        }

        // Fetch first 10
        Map<String, Object> first10 = getData(makeRequest("query { books(first: 10) { edges { node { name } } } }"), "books");
        List<Map<String, Object>> edges = (List<Map<String, Object>>) first10.get("edges");
        assertEquals(10, edges.size());

        // Fetch last 5
        Map<String, Object> last5 = getData(makeRequest("query { books(last: 5) { edges { node { name } } } }"), "books");
        assertEquals(5, ((List<?>) last5.get("edges")).size());

        // // Page with cursor
        // String afterCursor = (String) ((Map<String, Object>) first10.get("pageInfo")).get("endCursor");
        // Map<String, Object> after = getData(makeRequest(String.format("query { books(first: 5, after: \"%s\") { edges { node { name } } } }", afterCursor)), "books");
        // assertEquals(5, ((List<?>) after.get("edges")).size());
    }

    // Helper methods
    private Map<String, Object> makeRequest(String query) {
        try {
            String json = objectMapper.writeValueAsString(Map.of("query", query));
            HttpRequest<String> request = HttpRequest.POST("/", json);
            HttpResponse<Map<String, Object>> response = client.toBlocking().exchange(request, Argument.mapOf(String.class, Object.class));
            assertEquals(HttpStatus.OK, response.getStatus());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("GraphQL request failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getData(Map<String, Object> body, String key) {
        assertNotNull(body);
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertNotNull(data);
        Object val = data.get(key);
        assertNotNull(val);
        return (Map<String, Object>) val;
    }
}
