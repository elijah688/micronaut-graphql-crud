package example.micronaut.repository;

import jakarta.inject.Singleton;

import example.micronaut.model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Singleton
public class DbRepository {

    // In-memory maps for quick lookup and mutation
    private final Map<String, Book> bookMap = new ConcurrentHashMap<>();
    private final Map<String, Author> authorMap = new ConcurrentHashMap<>();

    public DbRepository() {
        // Initialize authors
        Author author1 = new Author("author-1", "Joanne", "Rowling");
        Author author2 = new Author("author-2", "Herman", "Melville");
        Author author3 = new Author("author-3", "Anne", "Rice");

        authorMap.put(author1.getId(), author1);
        authorMap.put(author2.getId(), author2);
        authorMap.put(author3.getId(), author3);

        // Initialize books with references to authors from authorMap
        Book book1 = new Book("book-1", "Harry Potter and the Philosopher's Stone", 223, author1);
        Book book2 = new Book("book-2", "Moby Dick", 635, author2);
        Book book3 = new Book("book-3", "Interview with the vampire", 371, author3);

        bookMap.put(book1.getId(), book1);
        bookMap.put(book2.getId(), book2);
        bookMap.put(book3.getId(), book3);
    }

    public List<Book> findAllBooks() {
        // Return books sorted by id (or any order you want)
        return bookMap.values().stream()
                .sorted(Comparator.comparing(Book::getId))
                .collect(Collectors.toList());
    }

    public Book findBookById(String id) {
        return bookMap.get(id);
    }

    public void saveBook(Book book) {
        bookMap.put(book.getId(), book);
    }

    public List<Author> findAllAuthors() {
        return new ArrayList<>(authorMap.values());
    }

    public Author findAuthorById(String id) {
        return authorMap.get(id);
    }
}
