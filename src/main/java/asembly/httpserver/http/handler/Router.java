package asembly.httpserver.http.handler;

import asembly.httpserver.http.response.JsonResponseService;

import java.util.HashMap;
import java.util.Map;

public class Router {

    private final Map<RouteKey, Handler> handlers = new HashMap<>();

    public void addHandler(String method, String path, SyncHandler handler) {
        handlers.put(new RouteKey(method, path), new SyncHandlerAdapter(handler));
    }

    public void addHandler(String method, String path, AsyncHandler handler) {
        handlers.put(new RouteKey(method, path), new AsyncHandlerAdapter(handler));
    }

    public Handler findHandler(String method, String path) {
        return handlers.getOrDefault(
                new RouteKey(method, path),
                new SyncHandlerAdapter(_ -> JsonResponseService.notFound("Handler not found", path))
        );
    }
}
