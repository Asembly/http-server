package asembly.httpserver.handler;

import asembly.httpserver.model.RouteKey;
import asembly.httpserver.util.RequestReader;

import java.net.Socket;
import java.util.Map;

public class ProxySocketHandler extends ConnectionHandler{

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
    }
}
