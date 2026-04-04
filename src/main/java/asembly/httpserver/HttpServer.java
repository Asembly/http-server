package asembly.httpserver;

import asembly.httpserver.config.ServerConfig;
import asembly.httpserver.http.io.RequestReader;
import asembly.httpserver.route.RouteDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class HttpServer {

    private static final Logger log = LoggerFactory.getLogger(HttpServer.class);
    private final InetAddress address;
    private final int port;
    private final int backlog;
    private final RouteDispatcher dispatcher;
    private final RequestReader requestReader;

    private static ServerConfig config;

    public HttpServer(ServerConfig config) throws UnknownHostException {
        this.config = config;
        this.requestReader = new RequestReader();
        this.backlog = config.getBacklog();
        this.port = config.getPort();
        this.address = InetAddress.getByName(config.getHost());
        this.dispatcher = new RouteDispatcher();
    }

    public void start() throws IOException {
        try(var server = new ServerSocket(port, backlog, address))
        {
            log.info("Server started {}:{}", address.getHostAddress(), port);

            while(true)
            {
                Socket client = server.accept();
                var request = requestReader.read(client.getInputStream());
                client.setSoTimeout(config.getSoTimeout());
                dispatcher.handle(request, client);
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


}
