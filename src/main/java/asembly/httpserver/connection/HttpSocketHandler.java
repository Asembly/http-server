package asembly.httpserver.connection;

import asembly.httpserver.http.Request;
import asembly.httpserver.route.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class HttpSocketHandler extends ConnectionHandler{
    private static final Logger log = LoggerFactory.getLogger(HttpSocketHandler.class);

    private final Socket client;
    private final Router router;
    private final Request request;

    public HttpSocketHandler(Request request, Socket client, Router router) {
        this.client = client;
        this.router = router;
        this.request = request;
    }

    @Override
    public void run() {
        try(client; OutputStream output = client.getOutputStream())
        {
            log.info("Client connected {} {} {}", client.getInetAddress().getHostAddress(), request.getMethod(), request.getPath());
            var handler = router.findHandler(request.getMethod(), request.getPath());
            var response = handler.handle(request);
            sendResponse(response, output);
        }
        catch (IOException e) {
            log.warn(e.getMessage());
        }
    }
}
