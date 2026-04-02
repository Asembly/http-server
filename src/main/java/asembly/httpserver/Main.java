package asembly.httpserver;

import asembly.httpserver.config.ServerConfig;
import asembly.httpserver.config.ServerConfigLoader;
import asembly.httpserver.http.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            String configPath = args.length > 0 ? args[0] : "server.properties";
            ServerConfig config = ServerConfigLoader.load(configPath);

            log.info("The configuration file has been loaded.");

            HttpServer server = new HttpServer(config);

            for(var item: config.getRoutes().entrySet())
                server.addHandler("GET", item.getKey(), new StaticHandler(item.getValue()));

            server.start();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}