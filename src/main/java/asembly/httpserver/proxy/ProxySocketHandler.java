package asembly.httpserver.handler;

import asembly.httpserver.model.RouteKey;
import asembly.httpserver.util.RequestReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class ProxySocketHandler extends ConnectionHandler{

    private static final Logger log = LoggerFactory.getLogger(ProxySocketHandler.class);
    private final Map<RouteKey, Handler> handlers;

    private final Socket client;

    private final RequestReader requestReader;

    public ProxySocketHandler(Socket client, Map<RouteKey, Handler> handlers) {
        this.client = client;
        this.handlers = handlers;
        this.requestReader = new RequestReader();
    }

    @Override
    public void run() {
        try(client; OutputStream output = client.getOutputStream();
            InputStream input = client.getInputStream()) {
            var request = requestReader.read(input);
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
