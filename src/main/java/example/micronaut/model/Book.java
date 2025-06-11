package example.micronaut.model;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Book {

    private String id;
    private String name;
    private int pageCount;
    private Author author;

    public Book() {
        // no-arg constructor needed for frameworks/serialization
    }

    public Book(String id, String name, int pageCount, Author author) {
        this.id = id;
        this.name = name;
        this.pageCount = pageCount;
        this.author = author;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPageCount() {
        return pageCount;
    }

    public Author getAuthor() {
        return author;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
