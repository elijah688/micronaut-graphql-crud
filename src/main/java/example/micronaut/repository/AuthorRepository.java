package example.micronaut.repository;

import example.micronaut.model.Author;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorRepository extends CrudRepository<Author, UUID> {

    Optional<Author> findById(UUID id);

    @Query(value = """
            INSERT INTO authors (id, first_name, last_name, created_at, updated_at)
            VALUES (:id, :firstName, :lastName, :createdAt, now())
            ON CONFLICT (id) DO UPDATE
              SET first_name = EXCLUDED.first_name,
                  last_name = EXCLUDED.last_name,
                  updated_at = now()
            RETURNING *
            """, nativeQuery = true)
    Author upsert(UUID id, String firstName, String lastName, java.time.Instant createdAt);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
