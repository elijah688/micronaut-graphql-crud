package example.micronaut.service;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import example.micronaut.model.Author;
import example.micronaut.repository.AuthorRepository;
import jakarta.inject.Singleton;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
@Singleton
public class AuthorService {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author getAuthorById(UUID id) {
        return authorRepository.findById(id).orElse(null);
    }

    public Author upsertAuthor(Author author) {
        return authorRepository.upsert(author.getId(), author.getFirstName(), author.getLastName(), Instant.now());
    }
}
