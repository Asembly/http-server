package asembly.httpserver.config;

import asembly.httpserver.config.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class ServerConfig {

    public asembly.httpserver.config.entity.ServerConfig serverConfig;
    public DirConfig directory;
    public CacheConfig cacheConfig;
    public Map<String, ServiceConfig> upstream;
    public GzipConfig gzipConfig;
    public LoggingConfig loggingConfig;

    public static ServerConfig loadConfig(Path path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(path.toFile(), ServerConfig.class);
    }
}