package asembly.httpserver.config.entity;

public record LoggingConfig(
        String level,
        String accessLog,
        String errorLog
) {
}
