package asembly.httpserver.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;

public class ServerConfigLoader {
    public static ServerConfig load(String path) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            props.load(fis);
        }

        String host = props.getProperty("server.host", "0.0.0.0");
        int port = Integer.parseInt(props.getProperty("server.port", "8080"));
        int backlog = Integer.parseInt(props.getProperty("server.backlog", "1"));
        int threads = Integer.parseInt(props.getProperty("server.threads", "8"));
        int soTimeout = Integer.parseInt(props.getProperty("server.soTimeout", "5000"));
        String staticDir = props.getProperty("server.staticDir", "public");
        String proxyDir = props.getProperty("server.proxyDir", "api");
        boolean proxyEnabled = Boolean.parseBoolean(props.getProperty("proxy.enabled", "false"));
        Map<String, List<URI>> upstreams = new HashMap<>();
        Map<String, String> routes = new HashMap<>();

        String upstreamsPrefix = "proxy.upstreams.";
        for(String key: props.stringPropertyNames())
        {
            if(!key.startsWith(upstreamsPrefix)) continue;
            String serviceName = key.substring(upstreamsPrefix.length());
            String value = props.getProperty(key);

            List<URI> uris = Arrays.stream(value.split("\\s*,\\s*"))
                    .filter(s -> !s.isBlank())
                    .map(URI::create)
                    .toList();

            upstreams.put(serviceName, uris);
        }

        String prefix = "route.";
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                String route = key.substring(prefix.length());
                String file = props.getProperty(key);
                routes.put(route, file);
            }
        }

        return new ServerConfig(host, port, threads, staticDir, proxyEnabled, upstreams, backlog, routes, soTimeout);
    }
}
