package asembly.httpserver.handler;

import asembly.httpserver.enums.StatusCode;
import asembly.httpserver.model.RouteKey;
import asembly.httpserver.util.RequestWriter;
import asembly.httpserver.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class SocketHandler implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(SocketHandler.class);

    private final Map<RouteKey, Handler> handlers;

    private final Socket client;

    private final RequestWriter requestWriter;

    public SocketHandler(Socket client, Map<RouteKey, Handler> handlers) {
        this.client = client;
        this.handlers = handlers;
        this.requestWriter = new RequestWriter();
    }

    @Override
    public void run() {
        try(client; OutputStream output = client.getOutputStream();
        InputStream input = client.getInputStream())
        {
            var request = requestWriter.write(input);
            log.info("Client connected {} {} {}", client.getInetAddress().getHostAddress(), request.getMethod(), request.getPath());

            var handler = handlers.getOrDefault(
                    new RouteKey(
                            request.getMethod(),
                            request.getPath()
                    ),
                    (_, _)->{}
            );

            handler.handle(request, output);
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void send(Response response){
        StringBuilder sb = new StringBuilder();

        var statusCode = response.getStatusCode();

        sb.append("HTTP/1.1 ");
        sb.append(statusCode).append(" ");
        sb.append(StatusCode.stringFromCode(statusCode)).append("\r\n");

        for(var header: response.getHeaders().entrySet())
            sb.append(String.join(": ", header.getKey(), header.getValue())).append("\r\n");

        sb.append("\r\n");

        sb.append(response.getBody());

        try{
            response.getOutputStream().write(sb.toString().getBytes());
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
