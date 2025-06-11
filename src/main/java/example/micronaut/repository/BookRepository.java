package example.micronaut.repository;

import example.micronaut.model.Book;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.PageableRepository;
import io.micronaut.data.model.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends PageableRepository<Book, String> {

    Optional<Book> findById(String id);

    @Query("SELECT b FROM Book b WHERE b.id > :after ORDER BY b.id ASC")
    List<Book> findAllAfter(String after, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.id < :before ORDER BY b.id DESC")
    List<Book> findAllBefore(String before, Pageable pageable);

    @Query("SELECT b FROM Book b ORDER BY b.id ASC")
    List<Book> findAllAsc(Pageable pageable);

    @Query("SELECT b FROM Book b ORDER BY b.id DESC")
    List<Book> findAllDesc(Pageable pageable);

    <S extends Book> S save(S entity);

}
