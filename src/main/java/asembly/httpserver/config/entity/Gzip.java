package asembly.httpserver.config.entity;

import java.util.List;

public record Gzip(
        boolean enabled,
        int minBytes,
        List<String>types
) {
}
