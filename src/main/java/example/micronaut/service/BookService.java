package example.micronaut.service;

import example.micronaut.model.Book;
import example.micronaut.repository.BookRepository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Singleton;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Singleton
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getBooks(UUID before, UUID after, Integer first, Integer last) {
        int defaultSize = 10;

        if (after != null) {
            int size = first != null ? first : defaultSize;
            Pageable pageable = Pageable.from(0, size, Sort.of(Sort.Order.asc("id")));
            return bookRepository.findAllAfter(after, pageable);

        } else if (before != null) {
            int size = last != null ? last : defaultSize;
            Pageable pageable = Pageable.from(0, size, Sort.of(Sort.Order.desc("id")));
            List<Book> booksDesc = bookRepository.findAllBefore(before, pageable);
            Collections.reverse(booksDesc);
            return booksDesc;

        } else {
            int size = first != null ? first : (last != null ? last : defaultSize);

            if (last != null) {
                Pageable pageable = Pageable.from(0, size, Sort.of(Sort.Order.desc("id")));
                List<Book> booksDesc = bookRepository.findAllDesc(pageable);
                Collections.reverse(booksDesc);
                return booksDesc;
            } else {
                Pageable pageable = Pageable.from(0, size, Sort.of(Sort.Order.asc("id")));
                return bookRepository.findAllAsc(pageable);
            }
        }
    }

    public Book getBookById(UUID id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book upsertBook(Book book) {
        return bookRepository.upsert(book.getId(), book.getAuthor().getId(), book.getName(), book.getPageCount());
    }

}
