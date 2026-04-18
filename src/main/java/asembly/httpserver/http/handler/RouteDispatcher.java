package asembly.httpserver.http.handler;

import asembly.httpserver.HttpServer;
import asembly.httpserver.entity.ClientState;
import asembly.httpserver.http.Request;
import asembly.httpserver.service.ProxyService;

public class RouteDispatcher {

    private final Router router;

    public RouteDispatcher()
    {
        this.router = new Router();

        var config = HttpServer.config;

        router.addHandler("GET",  "/" + config.getStaticDir(), new StaticHandler());
    }

    public void handle(Request request, ClientState state, ProxyService proxyService)
    {
        var handler = router.findHandler(request.getMethod(), request.getBasePath());
        state.setResponse(handler.handle(request));

//        else if(handlePath.equals("/favicon.ico"))
//        {
//            var handler = router.findHandler(request.getMethod(), "/public"+request.getPath());
//            state.setResponse(handler.handle(request));
//        }
//        else if(request.getPath().startsWith("/api/"))
//        {
//            var handler = new ProxyHandler(request, proxyService);
//        }
    }

}
