public class GraphQLTestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.create();

    @SuppressWarnings("unchecked")
    public static String getBookName(Map<String, Object> edge) {
        Map<String, Object> node = (Map<String, Object>) edge.get("node");
        return (String) node.get("name");
    }

    public static Map<String, Object> makeRequest(String query) {
        try {
            String json = objectMapper.writeValueAsString(Map.of("query", query));
            HttpRequest<String> request = HttpRequest.POST("/", json);
            HttpResponse<Map<String, Object>> response = client.toBlocking().exchange(request,
                    Argument.mapOf(String.class, Object.class));
            if (response.getStatus() != HttpStatus.OK) {
                throw new RuntimeException("GraphQL request failed with status: " + response.getStatus());
            }
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("GraphQL request failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getData(Map<String, Object> body, String key) {
        if (body == null) throw new IllegalArgumentException("Response body is null");
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        if (data == null) throw new IllegalArgumentException("Data section missing");
        Object val = data.get(key);
        if (val == null) throw new IllegalArgumentException("Key '" + key + "' missing in data");
        return (Map<String, Object>) val;
    }
}
