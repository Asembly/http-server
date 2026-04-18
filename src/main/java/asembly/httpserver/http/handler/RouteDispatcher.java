package asembly.httpserver.http.handler;

import asembly.httpserver.HttpServer;
import asembly.httpserver.entity.ClientState;
import asembly.httpserver.http.Request;
import asembly.httpserver.service.ProxyService;

public class RouteDispatcher {

    private final Router router = new Router();;

    public RouteDispatcher()
    {
        router.addHandler("GET",  "/" + HttpServer.config.getStaticDir(), new StaticHandler());
    }

    public void handle(Request request, ClientState state, ProxyService proxyService)
    {
        var handler = router.findHandler(request.getMethod(), request.getBasePath());
        state.setResponse(handler.handle(request));
    }

}
