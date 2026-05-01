package asembly.httpserver.config.cache;

public record CacheConfig(
        boolean enabled,
        long maxBytes,
        long maxEntryBytes,
        long ttlSeconds
) {
}
