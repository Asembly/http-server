package asembly.httpserver.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
        String staticDir = props.getProperty("server.staticDir", "./public");
        boolean proxyEnabled = Boolean.parseBoolean(props.getProperty("proxy.enabled", "false"));
        String proxyUpstreams = props.getProperty("proxy.upstreams", "");
        Map<String,InetSocketAddress> upstreams = new HashMap<>();
        Map<String, String> routes = new HashMap<>();

        for(var item: proxyUpstreams.split(","))
        {
            URL url = new URL(item);
            upstreams.put(url.getPath(),new InetSocketAddress(url.getHost(), url.getPort()));
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
