package example.micronaut.repository;

import example.micronaut.model.Book;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.PageableRepository;
import io.micronaut.data.model.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.annotation.Repository;

@Repository
public interface BookRepository extends PageableRepository<Book, UUID> {

    Optional<Book> findById(UUID id);

    @Query("SELECT b FROM Book b WHERE b.id > :after ORDER BY b.id ASC")
    List<Book> findAllAfter(UUID after, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.id < :before ORDER BY b.id DESC")
    List<Book> findAllBefore(UUID before, Pageable pageable);

    @Query("SELECT b FROM Book b ORDER BY b.id ASC")
    List<Book> findFirstBooks(Pageable pageable);

    @Query("SELECT b FROM Book b ORDER BY b.id DESC")
    List<Book> findLastBooks(Pageable pageable);

    @Query(value = """
            INSERT INTO books (id, author_id, name, page_count, created_at, updated_at)
            VALUES (:id, :authorId, :name, :pageCount, :createdAt, now())
            ON CONFLICT (id) DO UPDATE
              SET author_id = EXCLUDED.author_id,
                  name = EXCLUDED.name,
                  page_count = EXCLUDED.page_count,
                  updated_at = now()
            RETURNING *
            """, nativeQuery = true)
    Book upsert(UUID id, UUID authorId, String name, Integer pageCount, java.time.Instant createdAt);

}
