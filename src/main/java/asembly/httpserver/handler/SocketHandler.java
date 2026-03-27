package asembly.httpserver.handler;

import asembly.httpserver.enums.StatusCode;
import asembly.httpserver.model.RouteKey;
import asembly.httpserver.util.RequestReader;
import asembly.httpserver.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SocketHandler implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(SocketHandler.class);

    private final Map<RouteKey, Handler> handlers;

    private final Socket client;

    private final RequestReader requestReader;

    public SocketHandler(Socket client, Map<RouteKey, Handler> handlers) {
        this.client = client;
        this.handlers = handlers;
        this.requestReader = new RequestReader();
    }

    @Override
    public void run() {
        try(client; OutputStream output = client.getOutputStream();
        InputStream input = client.getInputStream())
        {
            var request = requestReader.read(input);
            log.info("Client connected {} {} {}", client.getInetAddress().getHostAddress(), request.getMethod(), request.getPath());

            var handler = handlers.getOrDefault(
                    new RouteKey(
                            request.getMethod(),
                            request.getPath()
                    ),
                    (_)-> null
            );

            var response = handler.handle(request);
            send(response, output);
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void send(Response response, OutputStream output){

        if(response == null)
            return;

        StringBuilder sb = new StringBuilder();

        var statusCode = response.getStatusCode();

        sb.append("HTTP/1.1 ");
        sb.append(statusCode).append(" ");
        sb.append(StatusCode.stringFromCode(statusCode)).append("\r\n");

        for(var header: response.getHeaders().entrySet())
            sb.append(String.join(": ", header.getKey(), header.getValue())).append("\r\n");

        sb.append("\r\n");

        sb.append(new String(response.getBody(), StandardCharsets.UTF_8));

        try{
            output.write(sb.toString().getBytes());
            output.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
