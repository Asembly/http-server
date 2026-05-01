package asembly.httpserver.config;

import asembly.httpserver.config.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class Config {

    public asembly.httpserver.config.entity.ServerConfig server;
    public DirConfig directory;
    public CacheConfig cache;
    public Map<String, ServiceConfig> upstream;
    public GzipConfig gzip;
    public LoggingConfig logging;

    public static Config loadConfig(Path path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(path.toFile(), Config.class);
    }
}