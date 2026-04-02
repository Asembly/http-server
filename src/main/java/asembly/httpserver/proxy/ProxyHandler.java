package asembly.httpserver.proxy;

import asembly.httpserver.http.handler.Handler;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ProxyHandler implements Handler {

    Map<String, InetSocketAddress> serverSockets = new HashMap<>();

    // TODO на доработку, возможно выпилю

    @Override
    public Response handle(Request request) {

        return null;
    }
}
