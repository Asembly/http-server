package asembly.httpserver.config.entity;

public record CacheConfig(
        boolean enabled,
        long maxBytes,
        long maxEntryBytes,
        long ttlSeconds
) {
}
