package example.micronaut.data;

import graphql.schema.DataFetcher;
import jakarta.inject.Singleton;
import example.micronaut.service.AuthorService;
import example.micronaut.service.BookService;
import example.micronaut.model.*;
import example.micronaut.model.BookConnection.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class DataFetchers {

    private static final Logger LOG = LoggerFactory.getLogger(DataFetchers.class);

    private final BookService bookService;
    private final AuthorService authorService;

    public DataFetchers(
            BookService bookService,
            AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    public DataFetcher<Book> getBookByIdDataFetcher() {
        return env -> {
            String bookId = env.getArgument("id");
            return bookService.getBookById(bookId);
        };
    }

    public DataFetcher<BookConnection> getBooksDataFetcher() {
        return env -> {
            Integer first = env.getArgument("first");
            Integer last = env.getArgument("last");
            String before = env.getArgument("before");
            String after = env.getArgument("after");

            List<Book> allBooks = bookService.getBooks(before, after, first, last);

            // Cursor helper for encoding/decoding
            class CursorHelper {
                String encode(String id) {
                    return Base64.getEncoder().encodeToString(id.getBytes());
                }

                String decode(String cursor) {
                    if (cursor == null)
                        return null;
                    return new String(Base64.getDecoder().decode(cursor));
                }
            }
            CursorHelper cursorHelper = new CursorHelper();

            // Build edges
            List<BookEdge> edges = allBooks.stream()
                    .map(book -> new BookEdge(cursorHelper.encode(book.getId()), book))
                    .collect(Collectors.toList());

            String startCursor = edges.isEmpty() ? null : edges.get(0).getCursor();
            String endCursor = edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor();

            // hasNextPage and hasPreviousPage logic simplified (could be improved)
            boolean hasNextPage = false;
            boolean hasPreviousPage = false;

            PageInfo pageInfo = new PageInfo(startCursor, endCursor, hasNextPage, hasPreviousPage);
            BookConnection bookConnection = new BookConnection(edges, pageInfo);

            LOG.info("Fetched {} books with pagination", edges.size());

            return bookConnection;
        };
    }

    public DataFetcher<Book> upsertBookDataFetcher() {
        return env -> {
            Map<String, Object> input = env.getArgument("input");

            String id = (String) input.get("id");
            String name = (String) input.get("name");
            Integer pageCount = (Integer) input.get("pageCount");
            String authorId = (String) input.get("authorId");

            Book book = id != null ? bookService.getBookById(id) : null;
            if (book == null) {
                book = new Book();
                book.setId(UUID.randomUUID().toString());
            }

            book.setName(name);
            book.setPageCount(pageCount);

            Author author = authorService.getAuthorById(authorId);
            if (author == null) {
                throw new RuntimeException("Author not found for id: " + authorId);
            }
            book.setAuthor(author);

            return bookService.upsertBook(book);
        };
    }

    public DataFetcher<Author> getAuthorByIdDataFetcher() {
        return env -> {
            String authorId = env.getArgument("id");
            return authorService.getAuthorById(authorId);
        };
    }

    public DataFetcher<Author> upsertAuthorDataFetcher() {
        return env -> {
            Map<String, Object> input = env.getArgument("input");

            String id = (String) input.get("id");
            String firstName = (String) input.get("firstName");
            String lastName = (String) input.get("lastName");

            Author author = id != null ? authorService.getAuthorById(id) : null;
            if (author == null) {
                author = new Author();
                author.setId(UUID.randomUUID().toString());
            }

            author.setFirstName(firstName);
            author.setLastName(lastName);

            return authorService.upsertAuthor(author);
        };
    }
}
