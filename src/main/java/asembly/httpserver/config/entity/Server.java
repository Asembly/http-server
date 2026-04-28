package asembly.httpserver.config.entity;

public record Server(
        String host,
        String root,
        int port,
        int threads,
        int readTimeoutMs,
        int writeTimeoutMs
) {
}
