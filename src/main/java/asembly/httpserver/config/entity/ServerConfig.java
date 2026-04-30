package asembly.httpserver.config.entity;

public record ServerConfig(
        String host,
        String root,
        int port,
        int threads,
        int readTimeoutMs,
        int writeTimeoutMs
) {
}
