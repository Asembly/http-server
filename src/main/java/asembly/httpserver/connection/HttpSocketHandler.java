package asembly.httpserver.connection;

import asembly.httpserver.http.ResponseFabric;
import asembly.httpserver.http.handler.Handler;
import asembly.httpserver.model.RouteKey;
import asembly.httpserver.http.io.RequestReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class HttpSocketHandler extends ConnectionHandler{
    private static final Logger log = LoggerFactory.getLogger(HttpSocketHandler.class);

    private final Map<RouteKey, Handler> handlers;

    private final Socket client;

    private final RequestReader requestReader;

    public HttpSocketHandler(Socket client, Map<RouteKey, Handler> handlers) {
        this.client = client;
        this.handlers = handlers;
        this.requestReader = new RequestReader();
    }

    @Override
    public void run() {
        try(client; OutputStream output = client.getOutputStream();
        InputStream input = client.getInputStream())
        {
            var request = requestReader.read(input);
            log.info("Client connected {} {} {}", client.getInetAddress().getHostAddress(), request.getMethod(), request.getPath());

            var handler = handlers.getOrDefault(
                    new RouteKey(
                            request.getMethod(),
                            request.getPath()
                    ),
                    (_)-> ResponseFabric.notFound()
            );

            var response = handler.handle(request);
            send(response, output);
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
