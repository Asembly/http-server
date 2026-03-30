package asembly.httpserver;

import asembly.httpserver.config.ServerConfig;
import asembly.httpserver.handler.Handler;
import asembly.httpserver.handler.HttpSocketHandler;
import asembly.httpserver.model.RouteKey;
import asembly.httpserver.proxy.ProxySocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {


    private static final Logger log = LoggerFactory.getLogger(HttpServer.class);
    private final InetAddress address;
    private final int port;
    private final int backlog;

    private static ServerConfig config;

    private final Map<RouteKey, Handler> handlers;
    ExecutorService pool = Executors.newFixedThreadPool(100);

    public HttpServer(String address, int port, int backlog) throws UnknownHostException {
        this.address = InetAddress.getByName(address);
        this.port = port;
        this.backlog = backlog;
        this.handlers = new HashMap<>();
    }

    public HttpServer(ServerConfig config) throws UnknownHostException {
        this(config.getHost(), config.getPort(), config.getBacklog());
        this.config = config;
    }

    public void start() throws IOException {
        try(var server = new ServerSocket(port, backlog, address))
        {
            log.info("Server started {}:{}", address.getHostAddress(), port);

            if(config.isProxyEnabled())
                log.info("The server is running in proxy mode");
            else
                log.info("The server is running in default mode");

            while(true)
            {
                Socket client = server.accept();
                client.setSoTimeout(5000);

                Runnable task = config.isProxyEnabled()
                        ? new ProxySocketHandler(client, handlers)
                        : new HttpSocketHandler(client, handlers);

                pool.submit(task);
            }
        }
    }

    public static ServerConfig getConfig()
    {
        return HttpServer.config;
    }

    public void stop()
    {
    }

    public void addHandler(String method, String path, Handler handler)
    {
        var routeKey = new RouteKey(method, path);
        handlers.put(routeKey, handler);
    }

}
