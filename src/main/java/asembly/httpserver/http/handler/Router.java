package asembly.httpserver.http.handler;

import asembly.httpserver.http.response.JsonResponseService;

import java.util.HashMap;
import java.util.Map;

public class Router {

    private final Map<RouteKey, SyncHandler> handlers = new HashMap<>();

    public void addHandler(String method, String path, SyncHandler syncHandler) {
        handlers.put(new RouteKey(method, path), syncHandler);
    }

    public SyncHandler findHandler(String method, String path) {
        return handlers.getOrDefault(
                new RouteKey(method, path),
                _ -> JsonResponseService.notFound("Handler not found", path)
        );
    }
}
