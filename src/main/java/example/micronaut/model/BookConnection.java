package example.micronaut.model;

import io.micronaut.core.annotation.Introspected;

import java.util.List;

@Introspected
public class BookConnection {

    private List<BookEdge> edges;
    private PageInfo pageInfo;

    public BookConnection() {
    }

    public BookConnection(List<BookEdge> edges, PageInfo pageInfo) {
        this.edges = edges;
        this.pageInfo = pageInfo;
    }

    public List<BookEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<BookEdge> edges) {
        this.edges = edges;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Override
    public String toString() {
        return "BookConnection{" +
                "edges=" + edges +
                ", pageInfo=" + pageInfo +
                '}';
    }

    @Introspected
    public static class BookEdge {
        private String cursor;
        private Book node;

        public BookEdge() {
        }

        public BookEdge(String cursor, Book node) {
            this.cursor = cursor;
            this.node = node;
        }

        public String getCursor() {
            return cursor;
        }

        public void setCursor(String cursor) {
            this.cursor = cursor;
        }

        public Book getNode() {
            return node;
        }

        public void setNode(Book node) {
            this.node = node;
        }

        @Override
        public String toString() {
            return "BookEdge{" +
                    "cursor='" + cursor + '\'' +
                    ", node=" + node +
                    '}';
        }
    }

    @Introspected
    public static class PageInfo {
        private String startCursor;
        private String endCursor;
        private boolean hasNextPage;
        private boolean hasPreviousPage;

        public PageInfo() {
        }

        public PageInfo(String startCursor, String endCursor, boolean hasNextPage, boolean hasPreviousPage) {
            this.startCursor = startCursor;
            this.endCursor = endCursor;
            this.hasNextPage = hasNextPage;
            this.hasPreviousPage = hasPreviousPage;
        }

        public String getStartCursor() {
            return startCursor;
        }

        public void setStartCursor(String startCursor) {
            this.startCursor = startCursor;
        }

        public String getEndCursor() {
            return endCursor;
        }

        public void setEndCursor(String endCursor) {
            this.endCursor = endCursor;
        }

        public boolean isHasNextPage() {
            return hasNextPage;
        }

        public void setHasNextPage(boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
        }

        public boolean isHasPreviousPage() {
            return hasPreviousPage;
        }

        public void setHasPreviousPage(boolean hasPreviousPage) {
            this.hasPreviousPage = hasPreviousPage;
        }
    }
}
