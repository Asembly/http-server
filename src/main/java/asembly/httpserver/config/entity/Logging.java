package asembly.httpserver.config.entity;

public record Logging(
        String level,
        String accessLog,
        String errorLog
) {
}
