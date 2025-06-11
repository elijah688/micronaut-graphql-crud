package example.micronaut;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.micronaut.repository.BookRepository;
import example.micronaut.repository.UserRepository;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(application = Application.class, packages = "example.micronaut", transactional = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class GraphQLIntegrationTest {

    @Inject
    @Client("/graphql")
    HttpClient graphqlClient;

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    BookRepository bookRepo;

    @Inject
    UserRepository userRepo;

    protected GraphQLTestHelper helper;

    protected static String jwtToken;

    protected static final String TEST_USERNAME = "testuser";
    protected static final String TEST_PASSWORD = "testpass123";

    @BeforeAll
    void setup() throws Exception {
        userRepo.deleteAll();
        jwtToken = AuthTestUtils.getJwtToken(httpClient, TEST_USERNAME, TEST_PASSWORD);
        helper = new GraphQLTestHelper(graphqlClient, jwtToken);
    }
}
