package asembly.httpserver.http.handler.proxy;

import asembly.httpserver.http.Request;
import asembly.httpserver.http.RequestSerializer;
import asembly.httpserver.http.Response;
import asembly.httpserver.http.handler.Handler;
import asembly.httpserver.http.io.ResponseReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class ProxyHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(ProxyHandler.class);

    private final Request request;
    private final ResponseReader responseReader;
    private final ProxyService proxyService;

    public ProxyHandler(Request request, ProxyService proxyService) {
        this.request = request;
        this.responseReader = new ResponseReader();
        this.proxyService = proxyService;
    }

    // TODO доделать работу proxy
    @Override
    public Response handle(Request request) {

        try {
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

            if (routeUpstream != null) {
                InetSocketAddress upstreamAddress = new InetSocketAddress(routeUpstream.getHost(), routeUpstream.getPort());
                var buffer = RequestSerializer.toByteBuffer(request);
                proxy(buffer, upstreamAddress);
            }
//        else
//            sendResponse(ResponseFabric.notFound(), output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void proxy(ByteBuffer buffer, InetSocketAddress upstreamAddress) throws IOException {
        SocketChannel otherClient = SocketChannel.open();
        otherClient.configureBlocking(false);
        otherClient.connect(upstreamAddress);
        otherClient.write(buffer);

        if(buffer.hasRemaining())
            return;



        System.out.println("hello");

    }
}