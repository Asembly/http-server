package asembly.httpserver.http.handler;

import asembly.httpserver.HttpServer;
import asembly.httpserver.entity.ClientState;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.handler.proxy.ProxyHandler;
import asembly.httpserver.http.handler.proxy.ProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RouteDispatcher {

    private static final Logger log = LoggerFactory.getLogger(RouteDispatcher.class);
    private final ExecutorService pool;

    private final Router router;

    public RouteDispatcher()
    {
        this.router = new Router();

        var config = HttpServer.config;

        pool = Executors.newFixedThreadPool(config.getThreads());

        for(var item: config.getRoutes().entrySet())
            router.addHandler("GET",  "/" + config.getStaticDir() + item.getKey(), new StaticHandler(item.getValue()));
    }

    public void handle(Request request, ClientState state, ProxyService proxyService)
    {
        if(request.getPath().startsWith("/public/"))
        {
            var handler = router.findHandler(request.getMethod(), request.getPath());
            state.setResponse(handler.handle(request));
        }
        else if(request.getPath().equals("/favicon.ico"))
        {
            log.debug(request.getPath());
            var handler = router.findHandler(request.getMethod(), "/public"+request.getPath());
            state.setResponse(handler.handle(request));
        }
        else if(request.getPath().startsWith("/api/"))
        {
            var handler = new ProxyHandler(request, proxyService);
            state.setResponse(handler.handle(request));
        }
    }

}
