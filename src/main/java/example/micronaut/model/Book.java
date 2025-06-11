package example.micronaut.model;

import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.*;

@Introspected
@Entity
@Table(name = "books")
public class Book {

    @Id
    private String id;

    private String name;

    private int pageCount;

    @Embedded
    private Author author;

    public Book() {
    }

    public Book(String id, String name, int pageCount, Author author) {
        this.id = id;
        this.name = name;
        this.pageCount = pageCount;
        this.author = author;
    }

    // Getters/setters omitted for brevity

    @Override
    public String toString() {
        return "Book{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", pageCount=" + pageCount +
               ", author=" + author +
               '}';
    }

    @Embeddable
    @Introspected
    public static class Author {
        private String id;
        private String firstName;
        private String lastName;

        public Author() {}

        public Author(String id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        // Getters/setters omitted

        @Override
        public String toString() {
            return "Author{" +
                   "id='" + id + '\'' +
                   ", firstName='" + firstName + '\'' +
                   ", lastName='" + lastName + '\'' +
                   '}';
        }
    }
}
