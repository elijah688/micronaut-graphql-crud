package example.micronaut.utils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CursorUtil {

    public static record Cursor(Instant createdAt, UUID id) {}

    public static Cursor decode(String cursor) {
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            Pattern pattern = Pattern.compile("createdAt:(.+?)\\|id:(.+)");
            Matcher matcher = pattern.matcher(decoded);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid cursor format");
            }
            Instant createdAt = Instant.parse(matcher.group(1));
            UUID id = UUID.fromString(matcher.group(2));
            return new Cursor(createdAt, id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decode cursor: " + cursor, e);
        }
    }

    public static String encode(Instant createdAt, UUID id) {
        String raw = "createdAt:" + createdAt + "|id:" + id;
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
