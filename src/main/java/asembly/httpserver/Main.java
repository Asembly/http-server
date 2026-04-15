package asembly.httpserver;

import asembly.httpserver.config.ServerConfig;
import asembly.httpserver.config.ServerConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            String configPath = args.length > 0 ? args[0] : "server.properties";
            ServerConfig config = ServerConfigLoader.load(configPath);

            log.info("The configuration file has been loaded.");

            HttpServer server = new HttpServer(config);

            server.start();

        } catch (IOException e) {
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }
}