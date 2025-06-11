package example.micronaut.data;

import graphql.schema.DataFetcher;
import jakarta.inject.Singleton;
import example.micronaut.service.AuthorService;
import example.micronaut.service.BookService;
import example.micronaut.model.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Singleton
public class DataFetchers {

    private static final Logger LOG = LoggerFactory.getLogger(DataFetchers.class);

    private final BookService bookService;
    private final AuthorService authorService;

    public DataFetchers(BookService bookService, AuthorService authorService) {
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

        BookConnection bookConnection = bookService.getBooksConnection(before, after, first, last);

        LOG.info("Fetched {} books with pagination", 
            bookConnection.getEdges() != null ? bookConnection.getEdges().size() : 0);

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

            Book book = (id != null) ? bookService.getBookById(UUID.fromString(id)) : null;
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

            Author author = new Author(UUID.randomUUID(), firstName, lastName );
            if (id != null) {
                author.setId(UUID.fromString(id));
            }

            return authorService.upsertAuthor(author);
        };
    }
}
