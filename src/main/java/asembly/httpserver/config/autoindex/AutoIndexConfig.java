package asembly.httpserver.config.autoindex;

import java.util.List;

public record AutoIndexConfig(
        boolean enabled,
        String format,
        List<LocationConfig> locations
) { }
