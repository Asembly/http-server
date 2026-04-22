package asembly.httpserver.config;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class ServerConfig {
    private final String host;
    private final int port;
    private final int threads;
    private final String staticDir;
    private final boolean proxyEnabled;
    private final int soTimeout;
    Map<String, List<URI>> upstreams;
    private final Map<String, String> routes;

    public ServerConfig(String host, int port, int threads,
                        String staticDir, boolean proxyEnabled, Map<String, List<URI>> upstreams,
                        Map<String, String> routes, int soTimeout) {
        this.host = host;
        this.port = port;
        this.threads = threads;
        this.soTimeout = soTimeout;
        this.staticDir = staticDir;
        this.proxyEnabled = proxyEnabled;
        this.upstreams = upstreams;
        this.routes = routes;
    }

    public String getHost() { return host; }
    public int getPort() { return port; }
    public int getThreads() { return threads; }
    public int getSoTimeout() { return soTimeout; }
    public String getStaticDir() { return staticDir; }
    public boolean isProxyEnabled() { return proxyEnabled; }
    public Map<String, List<URI>> getProxyUpstreams() { return upstreams; }
    public Map<String, String> getRoutes() { return routes; }
}
