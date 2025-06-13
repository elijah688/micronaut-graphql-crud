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
            return bookService.getBookById(UUID.fromString(bookId));
        };
    }

    public DataFetcher<BookConnection> getBooksDataFetcher() {
        return env -> {
            Integer first = env.getArgument("first");
            Integer last = env.getArgument("last");
            String before = env.getArgument("before");
            String after = env.getArgument("after");

            UUID beforeUuid = before != null ? UUID.fromString(before) : null;
            UUID afterUuid = after != null ? UUID.fromString(after) : null;

            List<Book> allBooks = bookService.getBooks(beforeUuid, afterUuid, first, last);

            LOG.info(allBooks.toString());
            LOG.info(allBooks.toString());
            LOG.info(allBooks.toString());
            LOG.info(allBooks.toString());
            // Cursor helper for encoding/decoding
            class CursorHelper {
                String encode(UUID id) {
                    return Base64.getEncoder().encodeToString(id.toString().getBytes());
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

            Book book = id != null ? bookService.getBookById(UUID.fromString(id)) : null;
            if (book == null) {
                book = new Book();
                book.setId(UUID.randomUUID());
            }

            book.setName(name);
            book.setPageCount(pageCount);

            Author author = authorService.getAuthorById(UUID.fromString(authorId));
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
            return authorService.getAuthorById(UUID.fromString(authorId));
        };
    }

    public DataFetcher<Author> upsertAuthorDataFetcher() {
        return env -> {
            Map<String, Object> input = env.getArgument("input");

            String id = (String) input.get("id");
            String firstName = (String) input.get("firstName");
            String lastName = (String) input.get("lastName");

            Author author = new Author(UUID.randomUUID(), firstName, lastName);

            if (id != null) {
                author.setId(UUID.fromString(id));
            }

            return authorService.upsertAuthor(author);
        };
    }
}
