package asembly.httpserver.http.handler;

import asembly.httpserver.HttpServer;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;
import asembly.httpserver.http.handler.proxy.ProxyHandler;
import asembly.httpserver.service.ProxyService;
import asembly.httpserver.state.ClientState;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public class RouteDispatcher {

    private final Router router = new Router();;
    private final ProxyService proxyService = new ProxyService();

    public RouteDispatcher()
    {
        var staticHandler = new StaticHandler();
        var proxyHandler = new ProxyHandler(proxyService);
        router.addHandler("GET",  "/" + HttpServer.config.getStaticDir(), staticHandler);
        router.addHandler("GET",  "/favicon.ico", staticHandler);
        router.addHandler("GET", "/api", proxyHandler);
        router.addHandler("POST", "/api", proxyHandler);
    }

    public void handle(Request request, ClientState state, SelectionKey key) throws IOException {
        var handler = router.findHandler(request.getMethod(), request.getBasePath());

        if(!handler.isAsync())
        {
            Response response = handler.handleSync(request);
            state.setResponse(response);
        }
        else {
            handler.handleAsync(request, key);
        }
    }
}
