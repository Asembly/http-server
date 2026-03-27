package asembly.httpserver;

import asembly.httpserver.config.ServerConfig;
import asembly.httpserver.config.ServerConfigLoader;
import asembly.httpserver.handler.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            String configPath = args.length > 0 ? args[0] : "server.properties";
            ServerConfig config = ServerConfigLoader.load(configPath);

            log.info("The configuration file has been loaded.");

            HttpServer server = new HttpServer(config);
            server.addHandler("GET", "/files", new FileHandler());
            server.addHandler("POST", "/files", new FileHandler());
            server.start();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}