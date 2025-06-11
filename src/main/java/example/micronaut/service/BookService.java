package example.micronaut.service;

import example.micronaut.model.Book;
import example.micronaut.repository.BookRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Singleton;

import java.util.Collections;
import java.util.List;

@Singleton
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getBooks(String before, String after, Integer first, Integer last) {
        int defaultSize = 10;

        if (after != null) {
            int size = first != null ? first : defaultSize;
            int pageNumber;
            try {
                pageNumber = Integer.parseInt(after);
            } catch (NumberFormatException e) {
                pageNumber = 0;
            }

            Pageable pageable = Pageable.from(pageNumber, size, Sort.of(Sort.Order.asc("id")));
            Page<Book> page = bookRepository.findAll(pageable);
            return page.getContent();

        } else if (before != null) {
            int size = last != null ? last : defaultSize;
            int pageNumber;
            try {
                pageNumber = Integer.parseInt(before);
            } catch (NumberFormatException e) {
                pageNumber = 0;
            }

            Pageable pageable = Pageable.from(pageNumber, size, Sort.of(Sort.Order.desc("id")));
            Page<Book> page = bookRepository.findAll(pageable);
            List<Book> booksDesc = page.getContent();
            Collections.reverse(booksDesc);
            return booksDesc;

        } else {
            if (first != null) {
                Pageable pageable = Pageable.from(0, first, Sort.of(Sort.Order.asc("id")));
                Page<Book> page = bookRepository.findAll(pageable);
                return page.getContent();
            } else if (last != null) {
                Pageable pageable = Pageable.from(0, last, Sort.of(Sort.Order.desc("id")));
                Page<Book> page = bookRepository.findAll(pageable);
                List<Book> booksDesc = page.getContent();
                Collections.reverse(booksDesc);
                return booksDesc;
            } else {
                Pageable pageable = Pageable.from(0, defaultSize, Sort.of(Sort.Order.asc("id")));
                Page<Book> page = bookRepository.findAll(pageable);
                return page.getContent();
            }
        }
    }

    public Book getBookById(String id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book upsertBook(Book book) {
        return bookRepository.save(book);
    }
}
