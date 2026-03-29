package asembly.httpserver.handler;

import asembly.httpserver.enums.StatusCode;
import asembly.httpserver.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class ConnectionHandler implements Runnable{

    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);

    public void send(Response response, OutputStream output)
    {
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
