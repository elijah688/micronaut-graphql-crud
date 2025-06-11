package example.micronaut;

import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookGraphQLIntegrationTest extends GraphQLIntegrationTest {

    private static String authorId;
    private static String bookId;

    @BeforeAll
    void setupAuthor() {
        userRepo.deleteAll();

        // Make sure author exists for book tests
        String query = "mutation { upsertAuthor(input: {firstName: \"John\", lastName: \"Doe\"}) { id } }";
        Map<String, Object> body = helper.makeRequest(query);
        Map<String, Object> author = helper.getData(body, "upsertAuthor");
        authorId = (String) author.get("id");
        assertNotNull(authorId);
    }

    @Test
    @Order(1)
    void createBook() {
        String query = String.format(
                "mutation { upsertBook(input: {name: \"GraphQL 101\", pageCount: 150, authorId: \"%s\"}) { id name pageCount author { id } } }",
                authorId);
        Map<String, Object> body = helper.makeRequest(query);
        Map<String, Object> book = helper.getData(body, "upsertBook");

        bookId = (String) book.get("id");
        assertEquals("GraphQL 101", book.get("name"));
        assertEquals(150, book.get("pageCount"));
        assertEquals(authorId, ((Map<String, Object>) book.get("author")).get("id"));
    }

    @Test
    @Order(2)
    void getBook() {
        String query = String.format("query { bookById(id: \"%s\") { id name pageCount author { id } } }", bookId);
        Map<String, Object> body = helper.makeRequest(query);
        Map<String, Object> book = helper.getData(body, "bookById");

        assertEquals(bookId, book.get("id"));
        assertEquals("GraphQL 101", book.get("name"));
        assertEquals(150, book.get("pageCount"));
    }

    @Test
    @Order(3)
    void upsertBook() {
        String query = String.format(
                "mutation { upsertBook(input: {id: \"%s\", name: \"GraphQL Advanced\", pageCount: 300, authorId: \"%s\"}) { id name pageCount } }",
                bookId, authorId);
        Map<String, Object> body = helper.makeRequest(query);
        Map<String, Object> book = helper.getData(body, "upsertBook");

        assertEquals(bookId, book.get("id"));
        assertEquals("GraphQL Advanced", book.get("name"));
        assertEquals(300, book.get("pageCount"));
    }

    @Test
    @Order(4)
    void paginationTest() {
        bookRepo.deleteAll();

        // Insert 25 books named "Book 01" to "Book 25"
        for (int i = 1; i <= 25; i++) {
            String name = String.format("Book %02d", i);
            String mutation = String.format(
                    "mutation { upsertBook(input: {name: \"%s\", pageCount: %d, authorId: \"%s\"}) { id } }",
                    name, i * 10, authorId);
            helper.makeRequest(mutation);
        }

        // 1) Fetch first 10 books
        Map<String, Object> first10Resp = helper.getData(helper.makeRequest("""
                query {
                    books(first: 10) {
                        edges { cursor node { name } }
                        pageInfo { startCursor endCursor hasNextPage hasPreviousPage }
                    }
                }
                """), "books");

        List<Map<String, Object>> edgesFirst10 = (List<Map<String, Object>>) first10Resp.get("edges");
        assertEquals(10, edgesFirst10.size());
        assertEquals("Book 01", helper.getBookName(edgesFirst10.get(0)));
        assertEquals("Book 10", helper.getBookName(edgesFirst10.get(9)));

        String afterCursor10 = (String) ((Map<String, Object>) first10Resp.get("pageInfo")).get("endCursor");

        // 2) Fetch next 5 books after Book 10 using after + first
        Map<String, Object> after10Next5 = helper.getData(helper.makeRequest(String.format("""
                query {
                    books(first: 5, after: "%s") {
                        edges { cursor node { name } }
                        pageInfo { startCursor endCursor hasNextPage hasPreviousPage }
                    }
                }
                """, afterCursor10)), "books");

        List<Map<String, Object>> edgesNext5 = (List<Map<String, Object>>) after10Next5.get("edges");
        assertEquals(5, edgesNext5.size());
        assertEquals("Book 11", helper.getBookName(edgesNext5.get(0)));
        assertEquals("Book 15", helper.getBookName(edgesNext5.get(4)));

        String after15Cursor = (String) ((Map<String, Object>) after10Next5.get("pageInfo")).get("endCursor");

        // 3) Fetch 5 books before Book 15 using before + last (backward pagination)
        Map<String, Object> before15Prev5 = helper.getData(helper.makeRequest(String.format("""
                query {
                    books(last: 5, before: "%s") {
                        edges { node { name } }
                        pageInfo { hasNextPage hasPreviousPage }
                    }
                }
                """, after15Cursor)), "books");

        List<Map<String, Object>> edgesBefore15 = (List<Map<String, Object>>) before15Prev5.get("edges");
        assertEquals(5, edgesBefore15.size());
        assertEquals("Book 10", helper.getBookName(edgesBefore15.get(0)));
        assertEquals("Book 14", helper.getBookName(edgesBefore15.get(4)));

        // 4) Fetch last 5 books overall
        Map<String, Object> last5Resp = helper.getData(helper.makeRequest("""
                query {
                    books(last: 5) {
                        edges { node { name } }
                        pageInfo { hasNextPage hasPreviousPage }
                    }
                }
                """), "books");

        List<Map<String, Object>> edgesLast5 = (List<Map<String, Object>>) last5Resp.get("edges");
        assertEquals(5, edgesLast5.size());
        assertEquals("Book 21", helper.getBookName(edgesLast5.get(0)));
        assertEquals("Book 25", helper.getBookName(edgesLast5.get(4)));

        // 5) Fetch middle 5 books after Book 05 using after + first
        String after5Cursor = (String) edgesFirst10.get(4).get("cursor"); // cursor at Book 05

        Map<String, Object> middle5 = helper.getData(helper.makeRequest(String.format("""
                query {
                    books(first: 5, after: "%s") {
                        edges { cursor node { name } }
                        pageInfo { startCursor endCursor }
                    }
                }
                """, after5Cursor)), "books");

        List<Map<String, Object>> middle5Edges = (List<Map<String, Object>>) middle5.get("edges");
        assertEquals(5, middle5Edges.size());
        assertEquals("Book 06", helper.getBookName(middle5Edges.get(0)));
        assertEquals("Book 10", helper.getBookName(middle5Edges.get(4)));

        String middle5EndCursor = (String) ((Map<String, Object>) middle5.get("pageInfo")).get("endCursor");

        // 6) Go back 5 books before the end cursor of the middle 5 using before + last
        Map<String, Object> rewind5 = helper.getData(helper.makeRequest(String.format("""
                query {
                    books(last: 5, before: "%s") {
                        edges { node { name } }
                    }
                }
                """, middle5EndCursor)), "books");

        List<Map<String, Object>> rewindEdges = (List<Map<String, Object>>) rewind5.get("edges");
        assertEquals(5, rewindEdges.size());
        // The rewind should give books 05 to 09, since it's before the cursor at Book
        // 10
        assertEquals("Book 05", helper.getBookName(rewindEdges.get(0)));
        assertEquals("Book 09", helper.getBookName(rewindEdges.get(4)));
    }
}
