package asembly.httpserver.config.entity;

import java.util.List;

public record GzipConfig(
        boolean enabled,
        int minBytes,
        List<String>types
) {
}
