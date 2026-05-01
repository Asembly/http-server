package asembly.httpserver.config.server;

public record ServerConfig(
        String host,
        String root,
        int port,
        int threads,
        int readTimeoutMs,
        int writeTimeoutMs
) {
}
