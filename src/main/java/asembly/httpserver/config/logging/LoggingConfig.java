package asembly.httpserver.config.logging;

public record LoggingConfig(
        String level,
        String accessLog,
        String errorLog
) {
}
