package asembly.httpserver.config;

public class ServerConfig {
    private final String host;
    private final int port;
    private final int threads;
    private final String staticDir;
    private final boolean proxyEnabled;

    public ServerConfig(String host, int port, int threads,
                        String staticDir, boolean proxyEnabled) {
        this.host = host;
        this.port = port;
        this.threads = threads;
        this.staticDir = staticDir;
        this.proxyEnabled = proxyEnabled;
    }

    public String getHost() { return host; }
    public int getPort() { return port; }
    public int getThreads() { return threads; }
    public String getStaticDir() { return staticDir; }
    public boolean isProxyEnabled() { return proxyEnabled; }
}
