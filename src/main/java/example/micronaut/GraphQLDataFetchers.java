package example.micronaut;

import graphql.schema.DataFetcher;
import jakarta.inject.Singleton;

import example.micronaut.repository.DbRepository;
import example.micronaut.model.*;
import example.micronaut.model.BookConnection.*;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GraphQLDataFetchers {
    private static final Logger LOG = LoggerFactory.getLogger(GraphQLDataFetchers.class);

    private final DbRepository dbRepository;

    public GraphQLDataFetchers(DbRepository dbRepository) {
        this.dbRepository = dbRepository;
    }

    public DataFetcher<Book> getBookByIdDataFetcher() {

        
        return env -> {
            String bookId = env.getArgument("id");

            return dbRepository.findBookById(bookId);
        };
    }

    public DataFetcher<BookConnection> getBooksDataFetcher() {
        return env -> {
            Integer first = env.getArgument("first");
            Integer last = env.getArgument("last");
            String before = env.getArgument("before");
            String after = env.getArgument("after");


         

            List<Book> allBooks = dbRepository.findAllBooks();

            // Helpers for cursor encoding/decoding (Base64)
            class CursorHelper {
                String encode(String id) {
                    return Base64.getEncoder().encodeToString(id.getBytes());
                }

                String decode(String cursor) {
                    if (cursor == null)
                        return null;
                    return new String(Base64.getDecoder().decode(cursor));
                }
            }

            CursorHelper cursorHelper = new CursorHelper();

            // Filter based on 'after' cursor
            String afterId = cursorHelper.decode(after);
            List<Book> filteredAfter = allBooks;
            if (afterId != null) {
                int afterIndex = -1;
                for (int i = 0; i < allBooks.size(); i++) {
                    if (allBooks.get(i).getId().equals(afterId)) {
                        afterIndex = i;
                        break;
                    }
                }
                if (afterIndex >= 0 && afterIndex + 1 < allBooks.size()) {
                    filteredAfter = allBooks.subList(afterIndex + 1, allBooks.size());
                } else {
                    filteredAfter = Collections.emptyList();
                }
            }

            // Filter based on 'before' cursor
            String beforeId = cursorHelper.decode(before);
            List<Book> filteredBefore = filteredAfter;
            if (beforeId != null) {
                int beforeIndex = -1;
                for (int i = 0; i < filteredAfter.size(); i++) {
                    if (filteredAfter.get(i).getId().equals(beforeId)) {
                        beforeIndex = i;
                        break;
                    }
                }
                if (beforeIndex >= 0) {
                    filteredBefore = filteredAfter.subList(0, beforeIndex);
                } else {
                    filteredBefore = Collections.emptyList();
                }
            }

            // Apply first and last
            List<Book> pagedBooks = filteredBefore;
            if (first != null) {
                pagedBooks = pagedBooks.stream().limit(first).collect(Collectors.toList());
            } else if (last != null) {
                int size = pagedBooks.size();
                pagedBooks = pagedBooks.stream()
                        .skip(Math.max(0, size - last))
                        .collect(Collectors.toList());
            }

            // Build edges
            List<BookEdge> edges = pagedBooks.stream()
                    .map(book -> new BookEdge(cursorHelper.encode(book.getId()), book))
                    .collect(Collectors.toList());


           
            String startCursor = edges.isEmpty() ? null : edges.get(0).getCursor();
            String endCursor = edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor();

            boolean hasNextPage = false;
            boolean hasPreviousPage = false;

            if (!pagedBooks.isEmpty()) {
                int firstIndex = allBooks.indexOf(pagedBooks.get(0));
                int lastIndex = allBooks.indexOf(pagedBooks.get(pagedBooks.size() - 1));
                hasPreviousPage = firstIndex > 0;
                hasNextPage = lastIndex < allBooks.size() - 1;
            }

            PageInfo pageInfo = new PageInfo(startCursor, endCursor, hasNextPage, hasPreviousPage);

            var x =  new BookConnection(edges, pageInfo);

             LOG.info("Pagination args -> first: {}, last: {}, before: '{}', after: '{}'", x.toString());
            LOG.info("Pagination args -> first: {}, last: {}, before: '{}', after: '{}'", x.toString());
            LOG.info("Pagination args -> first: {}, last: {}, before: '{}', after: '{}'", x.toString());
            LOG.info("Pagination args -> first: {}, last: {}, before: '{}', after: '{}'", x.toString());

            return x;
        };
    }

    public DataFetcher<Book> upsertBookDataFetcher() {
        return env -> {
            Map<String, Object> input = env.getArgument("input");

            String id = (String) input.get("id");
            String name = (String) input.get("name");
            Integer pageCount = (Integer) input.get("pageCount");
            String authorId = (String) input.get("authorId");

            Book book = id != null ? dbRepository.findBookById(id) : null;
            if (book == null) {
                book = new Book();
                book.setId(UUID.randomUUID().toString()); // generate new ID
            }

            book.setName(name);
            book.setPageCount(pageCount);

            Author author = dbRepository.findAuthorById(authorId);
            if (author == null) {
                throw new RuntimeException("Author not found for id: " + authorId);
            }
            book.setAuthor(author);

            dbRepository.saveBook(book);

            return book;
        };
    }

}
