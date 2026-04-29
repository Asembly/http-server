package asembly.httpserver.config;

import asembly.httpserver.config.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class ServerConfig {

    public Server server;
    public Dir directory;
    public Cache cache;
    public Map<String, Service> upstream;
    public Gzip gzip;
    public Logging logging;

    public static ServerConfig loadConfig(Path path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(path.toFile(), ServerConfig.class);
    }
}