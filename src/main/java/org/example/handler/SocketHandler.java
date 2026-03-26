package org.example.handler;

import org.example.enums.StatusCode;
import org.example.model.RouteKey;
import org.example.util.Request;
import org.example.util.Response;
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

    public SocketHandler(Socket client, Map<RouteKey, Handler> handlers) {
        this.client = client;
        this.handlers = handlers;
    }

    @Override
    public void run() {
        try(client; OutputStream output = client.getOutputStream();
        InputStream input = client.getInputStream())
        {
            var request = new Request(input);
            log.info("Client connected {} {} {}", client.getInetAddress().getHostAddress(), request.getMethod(), request.getUri());

            var handler = handlers.getOrDefault(
                    new RouteKey(
                            request.getMethod(),
                            Request.getPath(request.getUri())
                    ),
                    (_, _)->{}
            );

            var response = new Response();
            handler.handle(request, response);
            send(response, output);
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void send(Response response, OutputStream output){
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
            output.write(sb.toString().getBytes());
            output.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
