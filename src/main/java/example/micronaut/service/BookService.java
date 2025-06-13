package example.micronaut.service;

import example.micronaut.graphql.GraphQLFactory;
import example.micronaut.model.Book;
import example.micronaut.model.BookConnection;
import example.micronaut.model.BookConnection.BookEdge;
import example.micronaut.model.BookConnection.PageInfo;
import example.micronaut.repository.BookRepository;
import example.micronaut.utils.CursorUtil;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookConnection getBooksConnection(String beforeCursor, String afterCursor, Integer first, Integer last) {
        int defaultSize = 10;
        int size = (first != null) ? first : (last != null ? last : defaultSize);

        List<Book> books;
        boolean hasNextPage = false;
        boolean hasPreviousPage = false;

        if (afterCursor != null && first != null) {
            // Forward pagination
            CursorUtil.Cursor cursor = CursorUtil.decode(afterCursor);
            books = bookRepository.findAllAfter(cursor.createdAt(), cursor.id(), Pageable.from(0, size));

            if (!books.isEmpty()) {
                Book firstBook = books.get(0);
                Book lastBook = books.get(books.size() - 1);
                hasPreviousPage = bookRepository.existsBefore(firstBook.getCreatedAt(), firstBook.getId());
                hasNextPage = bookRepository.existsAfter(lastBook.getCreatedAt(), lastBook.getId());
            }

        } else if (beforeCursor != null && last != null) {
            // Backward pagination (rewind)
            CursorUtil.Cursor cursor = CursorUtil.decode(beforeCursor);

            List<Book> fetchedDesc = bookRepository.findAllBefore(cursor.createdAt(), cursor.id(),
                    Pageable.from(0, size + 1));
            Collections.reverse(fetchedDesc); // now ASC order

            // Take last N items (because DESC query gave most recent first)
            books = fetchedDesc.stream()
                    .skip(Math.max(0, fetchedDesc.size() - size))
                    .collect(Collectors.toList());

            if (!books.isEmpty()) {
                Book firstBook = books.get(0);
                Book lastBook = books.get(books.size() - 1);
                hasPreviousPage = bookRepository.existsBefore(firstBook.getCreatedAt(), firstBook.getId());
                hasNextPage = bookRepository.existsAfter(lastBook.getCreatedAt(), lastBook.getId());
            }

        } else if (last != null) {
            // Last page without cursor (last N books overall)
            List<Book> fetchedDesc = bookRepository.findLastBooks(Pageable.from(0, last + 1));
            Collections.reverse(fetchedDesc); // now ASC order

            books = fetchedDesc.stream()
                    .skip(Math.max(0, fetchedDesc.size() - last))
                    .collect(Collectors.toList());

            if (!books.isEmpty()) {
                Book firstBook = books.get(0);
                Book lastBook = books.get(books.size() - 1);
                hasPreviousPage = bookRepository.existsBefore(firstBook.getCreatedAt(), firstBook.getId());
                hasNextPage = bookRepository.existsAfter(lastBook.getCreatedAt(), lastBook.getId());
            }

        } else {
            // First page or forward pagination without cursor
            books = bookRepository.findFirstBooks(Pageable.from(0, size));

            if (!books.isEmpty()) {
                Book firstBook = books.get(0);
                Book lastBook = books.get(books.size() - 1);
                hasPreviousPage = bookRepository.existsBefore(firstBook.getCreatedAt(), firstBook.getId());
                hasNextPage = bookRepository.existsAfter(lastBook.getCreatedAt(), lastBook.getId());
            }
        }

        List<BookEdge> edges = books.stream()
                .map(book -> new BookEdge(book.toCursor(), book))
                .collect(Collectors.toList());

        String startCursor = edges.isEmpty() ? null : edges.get(0).getCursor();
        String endCursor = edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor();

        PageInfo pageInfo = new PageInfo(startCursor, endCursor, hasNextPage, hasPreviousPage);

        return new BookConnection(edges, pageInfo);
    }

    public Book getBookById(UUID id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book upsertBook(Book book) {
        return bookRepository.upsert(book.getId(), book.getAuthor().getId(), book.getName(), book.getPageCount(),
                Instant.now());
    }

}
