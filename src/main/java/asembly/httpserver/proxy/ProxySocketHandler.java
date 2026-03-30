package asembly.httpserver.proxy;

import asembly.httpserver.HttpServer;
import asembly.httpserver.handler.ConnectionHandler;
import asembly.httpserver.handler.Handler;
import asembly.httpserver.model.RouteKey;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.io.RequestReader;
import asembly.httpserver.http.io.ResponseReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ProxySocketHandler extends ConnectionHandler {

    private static final Logger log = LoggerFactory.getLogger(ProxySocketHandler.class);
    private final Map<RouteKey, Handler> handlers;

    private final Socket client;

    private final ResponseReader responseReader;
    private final RequestReader requestReader;

    public ProxySocketHandler(Socket client, Map<RouteKey, Handler> handlers) {
        this.client = client;
        this.handlers = handlers;
        this.responseReader = new ResponseReader();
        this.requestReader = new RequestReader();
    }

    @Override
    public void run() {

        try (client; OutputStream output = client.getOutputStream();
             InputStream input = client.getInputStream())
        {

            var request = requestReader.read(input);

            log.info("Client connected {} {} {}", client.getInetAddress().getHostAddress(), request.getMethod(), request.getPath());

            var upstreams = HttpServer.getConfig().getAddressUpstreamFromRoute();
            var routeUpstream = upstreams.get(request.getBasePath());

            if(routeUpstream != null)
                proxy(request, output, routeUpstream);

        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void proxy(Request clientRequest, OutputStream clientOutput, InetSocketAddress upstreamAddress) throws IOException {
        try(Socket upstream = new Socket(upstreamAddress.getAddress(), upstreamAddress.getPort()))
        {
            OutputStream upstreamOutput = upstream.getOutputStream();
            InputStream upstreamInput = upstream.getInputStream();

            sendToUpstream(clientRequest, upstreamOutput);

            var response = responseReader.read(upstreamInput);

            send(response, clientOutput);
        }
    }

    private void sendToUpstream(Request clientRequest, OutputStream upstreamOutput)
    {
        if(clientRequest == null)
            return;

        StringBuilder sb = new StringBuilder();

        sb.append(clientRequest.getMethod()).append(" ");
        sb.append(clientRequest.getPath()).append(" ");
        sb.append("HTTP/1.1").append("\r\n");

        for(var header: clientRequest.getHeaders().entrySet())
            sb.append(String.join(": ", header.getKey(), header.getValue())).append("\r\n");

        sb.append("\r\n");

        try{
            upstreamOutput.write(sb.toString().getBytes(StandardCharsets.UTF_8));

            if(clientRequest.getBody() != null && clientRequest.getBody().length > 0)
            {
                upstreamOutput.write(clientRequest.getBody());
            }

            upstreamOutput.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
