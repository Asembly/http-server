package asembly.httpserver.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
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
        String proxyUpstreams = props.getProperty("proxy.upstreams", "");
        List<InetSocketAddress> upstreams = new ArrayList<>();

        for(var item: proxyUpstreams.split(","))
        {
            item = item.trim();
            if(item.isEmpty()) continue;
            String[] parts = item.split(":");
            upstreams.add(new InetSocketAddress(parts[0], Integer.parseInt(parts[1])));
        }

        return new ServerConfig(host, port, threads, staticDir, proxyEnabled, upstreams);
    }
}
