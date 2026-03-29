package asembly.httpserver.config;

import java.net.InetSocketAddress;
import java.util.Map;

public class ServerConfig {
    private final String host;
    private final int port;
    private final int threads;
    private final String staticDir;
    private final boolean proxyEnabled;
    private final Map<String, InetSocketAddress> proxyUpstreams;

    public ServerConfig(String host, int port, int threads,
                        String staticDir, boolean proxyEnabled, Map<String, InetSocketAddress> proxyUpstreams) {
        this.host = host;
        this.port = port;
        this.threads = threads;
        this.staticDir = staticDir;
        this.proxyEnabled = proxyEnabled;
        this.proxyUpstreams = proxyUpstreams;
    }

    public String getHost() { return host; }
    public int getPort() { return port; }
    public int getThreads() { return threads; }
    public String getStaticDir() { return staticDir; }
    public boolean isProxyEnabled() { return proxyEnabled; }
    public Map<String, InetSocketAddress> getProxyUpstreams() { return proxyUpstreams; }
    public Map<String, InetSocketAddress> getAddressUpstreamFromRoute() { return proxyUpstreams; }
}
