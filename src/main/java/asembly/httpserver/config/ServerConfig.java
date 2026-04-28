package asembly.httpserver.config;

import asembly.httpserver.config.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ServerConfig {

    public Server server;
    public Dir directory;
    public Cache cache;
    public Map<String, List<Path>> upstream;
    public Gzip gzip;
    public Logging logging;

    public static ServerConfig loadConfig(java.nio.file.Path path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(path.toFile(), ServerConfig.class);
    }
}