package example.micronaut.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.io.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import example.micronaut.data.*;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Factory
public class GraphQLFactory {

    private static final Logger LOG = LoggerFactory.getLogger(GraphQLFactory.class);

    @Bean
    @Singleton
    public GraphQL graphQL(ResourceResolver resourceResolver,
            DataFetchers graphQLDataFetchers) {
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();

        Optional<InputStream> schemaStream = resourceResolver.getResourceAsStream("classpath:schema.graphql");
        if (schemaStream.isPresent()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(schemaStream.get()))) {
                typeRegistry.merge(schemaParser.parse(reader));
            } catch (Exception e) {
                LOG.error("Error reading GraphQL schema", e);
                throw new RuntimeException(e);
            }

            RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                    .type(newTypeWiring("Query")
                            .dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher())
                            .dataFetcher("books", graphQLDataFetchers.getBooksDataFetcher())
                            .dataFetcher("authorById", graphQLDataFetchers.getAuthorByIdDataFetcher())

                    )
                    .type(newTypeWiring("Mutation")
                            .dataFetcher("upsertBook", graphQLDataFetchers.upsertBookDataFetcher())
                            .dataFetcher("upsertAuthor", graphQLDataFetchers.upsertAuthorDataFetcher()))
                    .build();

            SchemaGenerator schemaGenerator = new SchemaGenerator();
            GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);

            return GraphQL.newGraphQL(graphQLSchema).build();
        } else {
            LOG.warn("GraphQL schema file not found, returning empty schema");
            return GraphQL.newGraphQL(GraphQLSchema.newSchema().build()).build();
        }
    }
}
