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
            INSERT INTO authors (id, first_name, last_name)
            VALUES (:id, :firstName, :lastName)
            ON CONFLICT (id) DO UPDATE
              SET first_name = EXCLUDED.first_name,
                  last_name = EXCLUDED.last_name
            RETURNING *
            """, nativeQuery = true)
    Author upsert(UUID id, String firstName, String lastName);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
