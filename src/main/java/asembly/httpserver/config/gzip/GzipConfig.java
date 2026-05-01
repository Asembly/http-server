package asembly.httpserver.config.gzip;

import java.util.List;

public record GzipConfig(
        boolean enabled,
        int minBytes,
        List<String>types
) {
}
