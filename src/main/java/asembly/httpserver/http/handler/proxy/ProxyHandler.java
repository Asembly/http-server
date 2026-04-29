package asembly.httpserver.http.handler.proxy;

import asembly.httpserver.http.Request;
import asembly.httpserver.http.handler.AsyncHandler;
import asembly.httpserver.service.ProxyService;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Arrays;

public class ProxyHandler implements AsyncHandler {
    private final ProxyService proxyService;

    public ProxyHandler(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Override
    public void handle(Request request, SelectionKey key) throws IOException {
        var path = request.getPath();

        if (path.startsWith("/"))
            path = path.substring(1);

        String[] parts = path.split("/");

        String serviceName = parts.length > 1 ? parts[1] : "";

        String routePath = parts.length > 2
                ? "/" + String.join("/", Arrays.copyOfRange(parts, 2, parts.length))
                : "/";

        var upstreamRequest = new Request.Builder()
                .addHeaders(request.getHeaders())
                .method(request.getMethod())
                .path(routePath)
                .version(request.getVersion())
                .boundary(request.getBoundary())
                .body(request.getBody())
                .addParams(request.getParams())
                .build();

        var routeUpstream = proxyService.getBalancer(serviceName).choose();



        proxyService.proxy(upstreamRequest, routeUpstream, key);
    }
}