package asembly.httpserver.http.handler;

import asembly.httpserver.http.ResponseFabric;

import java.util.HashMap;
import java.util.Map;

public class Router {

    private final Map<RouteKey, Handler> handlers = new HashMap<>();

    public void addHandler(String method, String path, Handler handler) {
        handlers.put(new RouteKey(method, path), handler);
    }

    public Handler findHandler(String method, String path) {
        return handlers.getOrDefault(
                new RouteKey(method, path),
                _ -> ResponseFabric.notFound()
        );
    }
}
