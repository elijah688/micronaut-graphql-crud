package example.micronaut.repository;

import example.micronaut.model.Author;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;
import io.micronaut.data.annotation.*;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AuthorRepository extends CrudRepository<Author, String> {

    Optional<Author> findById(String id);

    @Query("""
            INSERT INTO authors (id, name, bio)
            VALUES (:id, :name, :bio)
            ON CONFLICT (id) DO UPDATE
              SET name = EXCLUDED.name,
                  bio = EXCLUDED.bio
            """)
    void upsert(@BindBean Author author);

    boolean existsById(String id);

    void deleteById(String id);
}
