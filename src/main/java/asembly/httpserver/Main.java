package asembly.httpserver;

import asembly.httpserver.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        try {

            ServerConfig config = ServerConfig.loadConfig(Paths.get("config.json"));

            log.info("Config file is loaded");

            HttpServer server = new HttpServer(config);

            server.start();

        } catch (IOException e) {
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }
}