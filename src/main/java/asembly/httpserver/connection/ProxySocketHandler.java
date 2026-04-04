package asembly.httpserver.connection;

import asembly.httpserver.HttpServer;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.ResponseFabric;
import asembly.httpserver.http.io.ResponseReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.util.Arrays;

public class ProxySocketHandler extends ConnectionHandler {

    private static final Logger log = LoggerFactory.getLogger(ProxySocketHandler.class);

    private final Socket client;

    private final Request request;
    private final ResponseReader responseReader;

    public ProxySocketHandler(Request request, Socket client) {
        this.client = client;
        this.request = request;
        this.responseReader = new ResponseReader();
    }

    @Override
    public void run() {

        try (client; OutputStream output = client.getOutputStream())
        {

            log.info("Client connected {} {} {}", client.getInetAddress().getHostAddress(), request.getMethod(), request.getPath());

            var upstreams = HttpServer.getConfig().getProxyUpstreams();
            var path = request.getPath();


            if(path.startsWith("/"))
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

            var routeUpstream = upstreams.get(serviceName);

            // TODO сделать для начала стандартный балансировщик нагрузки,
            //  чтобы сервер не брал первый элемент а
            //  руководствовался выбором лучшего сервиса на данный момент

            if(routeUpstream != null)
                proxy(upstreamRequest, output, routeUpstream.get(0));
            else
                sendResponse(ResponseFabric.notFound(), output);

        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void proxy(Request clientRequest, OutputStream clientOutput, URI upstreamAddress) throws IOException {
        try(Socket upstream = new Socket(upstreamAddress.getHost(), upstreamAddress.getPort()))
        {
            OutputStream upstreamOutput = upstream.getOutputStream();
            InputStream upstreamInput = upstream.getInputStream();

            sendRequest(clientRequest, upstreamOutput);

            var response = responseReader.read(upstreamInput);

            sendResponse(response, clientOutput);
        }
    }


}
