package example.micronaut;

import com.fasterxml.jackson.databind.ObjectMapper;

import example.micronaut.repository.BookRepository;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(application = Application.class, packages = "example.micronaut", transactional = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorGraphQLIntegrationTest {

    @Inject
    @Client("/graphql")
    HttpClient client;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    DbInitializer dbInitializer;

    @Inject
    BookRepository bookRepo;

    @Inject
    javax.sql.DataSource dataSource;

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
        String query = String.format(
                "mutation { upsertAuthor(input: {id: \"%s\", firstName: \"Johnny\", lastName: \"Doe\"}) { id firstName lastName } }",
                authorId);
        Map<String, Object> body = makeRequest(query);
        Map<String, Object> author = getData(body, "upsertAuthor");

        assertEquals(authorId, author.get("id"));
        assertEquals("Johnny", author.get("firstName"));
    }

    @Test
    @Order(4)
    void createBook() {
        String query = String.format(
                "mutation { upsertBook(input: {name: \"GraphQL 101\", pageCount: 150, authorId: \"%s\"}) { id name pageCount author { id } } }",
                authorId);
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
        String query = String.format(
                "mutation { upsertBook(input: {id: \"%s\", name: \"GraphQL Advanced\", pageCount: 300, authorId: \"%s\"}) { id name pageCount } }",
                bookId, authorId);
        Map<String, Object> body = makeRequest(query);
        Map<String, Object> book = getData(body, "upsertBook");

        assertEquals(bookId, book.get("id"));
        assertEquals("GraphQL Advanced", book.get("name"));
        assertEquals(300, book.get("pageCount"));
    }

    @Test
    @Order(7)

    void paginationTest() {
        bookRepo.deleteAll();

        // Insert 25 books with names "Book 01" to "Book 25"
        for (int i = 1; i <= 25; i++) {
            String name = String.format("Book %02d", i);
            String mutation = String.format(
                    "mutation { upsertBook(input: {name: \"%s\", pageCount: %d, authorId: \"%s\"}) { id } }",
                    name, i * 10, authorId);
            makeRequest(mutation);
        }

        // Fetch first 10 books
        Map<String, Object> first10Resp = getData(makeRequest("""
                query {
                    books(first: 10) {
                        edges { cursor node { name } }
                        pageInfo { startCursor endCursor hasNextPage hasPreviousPage }
                    }
                }
                """), "books");

        List<Map<String, Object>> edgesFirst10 = (List<Map<String, Object>>) first10Resp.get("edges");
        assertEquals(10, edgesFirst10.size());
        assertEquals("Book 01", getBookName(edgesFirst10.get(0)));
        assertEquals("Book 10", getBookName(edgesFirst10.get(9)));

        String afterCursor10 = (String) ((Map<String, Object>) first10Resp.get("pageInfo")).get("endCursor");

        // Fetch next 5 after endCursor of first 10
        Map<String, Object> after10Next5 = getData(makeRequest(String.format("""
                query {
                    books(first: 5, after: "%s") {
                        edges { cursor node { name } }
                        pageInfo { startCursor endCursor hasNextPage hasPreviousPage }
                    }
                }
                """, afterCursor10)), "books");

        List<Map<String, Object>> edgesNext5 = (List<Map<String, Object>>) after10Next5.get("edges");
        assertEquals(5, edgesNext5.size());
        assertEquals("Book 11", getBookName(edgesNext5.get(0)));
        assertEquals("Book 15", getBookName(edgesNext5.get(4)));

        String after15Cursor = (String) ((Map<String, Object>) after10Next5.get("pageInfo")).get("endCursor");

        // Fetch 5 before Book 15 using `before` with last:5
        Map<String, Object> before15Prev5 = getData(makeRequest(String.format("""
                query {
                    books(last: 5, before: "%s") {
                        edges { node { name } }
                        pageInfo { hasNextPage hasPreviousPage }
                    }
                }
                """, after15Cursor)), "books");

        List<Map<String, Object>> edgesBefore15 = (List<Map<String, Object>>) before15Prev5.get("edges");
        assertEquals(5, edgesBefore15.size());
        assertEquals("Book 10", getBookName(edgesBefore15.get(0)));
        assertEquals("Book 14", getBookName(edgesBefore15.get(4)));

        // Fetch last 5 books
        Map<String, Object> last5Resp = getData(makeRequest("""
                query {
                    books(last: 5) {
                        edges { node { name } }
                        pageInfo { hasNextPage hasPreviousPage }
                    }
                }
                """), "books");

        List<Map<String, Object>> edgesLast5 = (List<Map<String, Object>>) last5Resp.get("edges");
        assertEquals(5, edgesLast5.size());
        assertEquals("Book 21", getBookName(edgesLast5.get(0)));
        assertEquals("Book 25", getBookName(edgesLast5.get(4)));

        // Fetch middle 5 books with after + first (after Book 05)
        String after5Cursor = (String) edgesFirst10.get(4).get("cursor"); // cursor after Book 05

        Map<String, Object> middle5 = getData(makeRequest(String.format("""
                query {
                    books(first: 5, after: "%s") {
                        edges { cursor node { name } }
                        pageInfo { startCursor endCursor }
                    }
                }
                """, after5Cursor)), "books");

        List<Map<String, Object>> middle5Edges = (List<Map<String, Object>>) middle5.get("edges");
        assertEquals(5, middle5Edges.size());
        assertEquals("Book 06", getBookName(middle5Edges.get(0)));
        assertEquals("Book 10", getBookName(middle5Edges.get(4)));

        String middle5EndCursor = (String) ((Map<String, Object>) middle5.get("pageInfo")).get("endCursor");

        // Go back with before + last (before middle5 end cursor)
        Map<String, Object> rewind5 = getData(makeRequest(String.format("""
                query {
                    books(last: 5, before: "%s") {
                        edges { node { name } }
                    }
                }
                """, middle5EndCursor)), "books");

        List<Map<String, Object>> rewindEdges = (List<Map<String, Object>>) rewind5.get("edges");

        rewindEdges.stream().forEach(e -> System.out.println(getBookName(e)));
        assertEquals(5, rewindEdges.size());
        assertEquals("Book 01", getBookName(rewindEdges.get(0)));
        assertEquals("Book 05", getBookName(rewindEdges.get(4)));
    }

    @SuppressWarnings("unchecked")
    private String getBookName(Map<String, Object> edge) {
        Map<String, Object> node = (Map<String, Object>) edge.get("node");
        return (String) node.get("name");
    }

    // Helper methods
    private Map<String, Object> makeRequest(String query) {
        try {
            String json = objectMapper.writeValueAsString(Map.of("query", query));
            HttpRequest<String> request = HttpRequest.POST("/", json);
            HttpResponse<Map<String, Object>> response = client.toBlocking().exchange(request,
                    Argument.mapOf(String.class, Object.class));
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
