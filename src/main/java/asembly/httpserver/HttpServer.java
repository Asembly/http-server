package asembly.httpserver;

import asembly.httpserver.handler.Handler;
import asembly.httpserver.handler.SocketHandler;
import asembly.httpserver.model.RouteKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {


    private static final Logger log = LoggerFactory.getLogger(HttpServer.class);
    private final InetAddress address;
    private final int port;
    private final int backlog;

    private final Map<RouteKey, Handler> handlers;

    public HttpServer(String address, int port, int backlog) throws UnknownHostException {
        this.address = InetAddress.getByName(address);
        this.port = port;
        this.backlog = backlog;
        this.handlers = new HashMap<>();
    }

    public HttpServer(String address, int port) throws UnknownHostException {
        this(address, port, 0);
    }

    public void start() throws IOException {
        try(var server = new ServerSocket(port, backlog, address))
        {
            log.info("Server started {}:{}", address.getHostAddress(), port);

            while(true)
            {
                Socket client = server.accept();
                new Thread(new SocketHandler(client, handlers)).start();
            }
        }
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
