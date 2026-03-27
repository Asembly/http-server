package asembly.httpserver.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerConfigLoader {
    public static ServerConfig load(String path) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            props.load(fis);
        }

        String host = props.getProperty("server.host", "0.0.0.0");
        int port = Integer.parseInt(props.getProperty("server.port", "8080"));
        int threads = Integer.parseInt(props.getProperty("server.threads", "8"));
        String staticDir = props.getProperty("server.staticDir", "./public");
        boolean proxyEnabled = Boolean.parseBoolean(props.getProperty("proxy.enabled", "false"));

        return new ServerConfig(host, port, threads, staticDir, proxyEnabled);
    }
}
