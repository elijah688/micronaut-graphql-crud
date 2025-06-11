package example.micronaut.service;

import example.micronaut.model.Author;
import example.micronaut.repository.AuthorRepository;
import jakarta.inject.Singleton;

@Singleton
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author getAuthorById(String id) {
        return authorRepository.findById(id).orElse(null);
    }

    public Author upsertAuthor(Author author) {
        return authorRepository.upsert(author);
    }
}
