package example.micronaut.service;

import example.micronaut.graphql.GraphQLFactory;
import example.micronaut.model.Book;
import example.micronaut.repository.BookRepository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Singleton;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class BookService {
    private static final Logger LOG = LoggerFactory.getLogger(GraphQLFactory.class);

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getBooks(UUID before, UUID after, Integer first, Integer last) {
        int defaultSize = 10;

        if (after != null) {
            int size = (first != null) ? first : defaultSize;
            return bookRepository.findAllAfter(after, Pageable.from(0, size));
        }

        if (before != null) {
            int size = (last != null) ? last : defaultSize;
            List<Book> books = bookRepository.findAllBefore(before, Pageable.from(0, size));
            Collections.reverse(books);
            return books;
        }

        if (last != null) {
            List<Book> books = bookRepository.findLastBooks(Pageable.from(0, last));
            Collections.reverse(books);
            return books;
        }

        int size = (first != null) ? first : defaultSize;
        return bookRepository.findFirstBooks(Pageable.from(0, size));
    }

    public Book getBookById(UUID id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book upsertBook(Book book) {
        return bookRepository.upsert(book.getId(), book.getAuthor().getId(), book.getName(), book.getPageCount());
    }

}
