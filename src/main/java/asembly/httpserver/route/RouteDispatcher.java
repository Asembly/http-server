package asembly.httpserver.route;

import asembly.httpserver.HttpServer;
import asembly.httpserver.connection.HttpSocketHandler;
import asembly.httpserver.connection.ProxySocketHandler;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.handler.StaticHandler;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RouteDispatcher {

    private final ExecutorService pool;

    private final Router router;

    public RouteDispatcher()
    {
        this.router = new Router();
        var config = HttpServer.getConfig();

        pool = Executors.newFixedThreadPool(config.getThreads());

        for(var item: config.getRoutes().entrySet())
            router.addHandler("GET",  "/" + config.getStaticDir() + item.getKey(), new StaticHandler(item.getValue()));
    }

    public void handle(Request request, Socket client)
    {

        if(request.getPath().startsWith("/api/"))
        {
            pool.submit(new ProxySocketHandler(request, client));
        }
        else if(request.getPath().startsWith("/public/"))
        {
            pool.submit(new HttpSocketHandler(request, client, router));
        }
    }

}
