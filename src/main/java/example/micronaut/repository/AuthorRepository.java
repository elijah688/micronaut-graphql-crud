package example.micronaut.repository;

import example.micronaut.model.Author;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@Repository
public interface AuthorRepository extends CrudRepository<Author, String> {

   
    Optional<Author> findById(String id);

  
    @Override
    <S extends Author> S save(S author);


    boolean existsById(String id);

  
    void deleteById(String id);
}
