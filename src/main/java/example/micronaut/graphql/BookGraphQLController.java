package example.micronaut.graphql;

import example.micronaut.model.Book;
import example.micronaut.service.BookService;
import io.micronaut.graphql.annotation.GraphQLRootResolver;
import io.micronaut.graphql.annotation.GraphQLQuery;
import io.micronaut.graphql.annotation.GraphQLMutation;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
@GraphQLRootResolver
public class BookGraphQLController {

    private final BookService bookService;

    public BookGraphQLController(BookService bookService) {
        this.bookService = bookService;
    }

    @GraphQLQuery
    public List<Book> books(String before, String after, Integer first, Integer last) {
        return bookService.getBooks(before, after, first, last);
    }

    @GraphQLQuery
    public Book bookById(String id) {
        return bookService.getBookById(id);
    }

    @GraphQLMutation
    public Book upsertBook(UpsertBookInput input) {
        Book book = new Book(input.getId(), input.getTitle(), input.getAuthor());
        return bookService.upsertBook(book);
    }
}
