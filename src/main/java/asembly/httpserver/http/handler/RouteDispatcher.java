package asembly.httpserver.http.handler;

import asembly.httpserver.HttpServer;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;
import asembly.httpserver.http.handler.proxy.ProxyHandler;
import asembly.httpserver.http.serialize.ResponseSerializer;
import asembly.httpserver.service.ProxyService;
import asembly.httpserver.http.state.ClientState;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public class RouteDispatcher {

    private final Router router = new Router();;

    public RouteDispatcher()
    {
        var staticHandler = new StaticHandler();
        ProxyService proxyService = new ProxyService();
        var proxyHandler = new ProxyHandler(proxyService);
        router.addHandler("get",  "/" + HttpServer.config.directory.publicRoot(), staticHandler);
        router.addHandler("get",  "/favicon.ico", staticHandler);
        router.addHandler("get", "/api", proxyHandler);
        router.addHandler("port", "/api", proxyHandler);

        var autoIndex =  HttpServer.config.auto_index;
        for(var location: autoIndex.locations())
            router.addHandler("get", location.alias(), new AutoIndexHandler(location.root(), autoIndex.format()));

    }

    public void handle(Request request, ClientState state, SelectionKey key) throws IOException {
        var handler = router.findHandler(request.getMethod(), request.getBasePath());

        if(!handler.isAsync())
        {
            Response response = handler.handleSync(request);
            state.setOutput(ResponseSerializer.toByteBuffer(response));
            key.interestOps(SelectionKey.OP_WRITE);
        }
        else {
            handler.handleAsync(request, key);
        }
    }
}
