package asembly.httpserver.config;

import asembly.httpserver.config.autoindex.AutoIndexConfig;
import asembly.httpserver.config.cache.CacheConfig;
import asembly.httpserver.config.gzip.GzipConfig;
import asembly.httpserver.config.logging.LoggingConfig;
import asembly.httpserver.config.server.DirConfig;
import asembly.httpserver.config.server.ServerConfig;
import asembly.httpserver.config.upstream.ServiceConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class Config {

    public ServerConfig server;
    public DirConfig directory;
    public CacheConfig cache;
    public Map<String, ServiceConfig> upstream;
    public GzipConfig gzip;
    public LoggingConfig logging;
    public AutoIndexConfig auto_index;

    public static Config loadConfig(Path path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(path.toFile(), Config.class);
    }
}