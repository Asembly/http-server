package asembly.httpserver.config.entity;

public record Cache(
        boolean enabled,
        long maxBytes,
        long maxEntryBytes,
        long ttlSeconds
) {
}
