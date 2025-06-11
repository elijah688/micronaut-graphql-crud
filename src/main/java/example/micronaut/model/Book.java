package example.micronaut.model;

import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

import example.micronaut.utils.CursorUtil;

@Introspected
@Entity
@Table(name = "books")
public class Book {

    @Id
    private UUID id;

    private String name;

    @Column(name = "page_count")
    private int pageCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private Author author;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Book() {
    }

    public Book(UUID id, String name, int pageCount, Author author, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.pageCount = pageCount;
        this.author = author;
        this.createdAt = createdAt;
    }

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Generate cursor string from this Book instance
    public String toCursor() {
        return CursorUtil.encode(this.createdAt, this.id);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pageCount=" + pageCount +
                ", author=" + author +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
