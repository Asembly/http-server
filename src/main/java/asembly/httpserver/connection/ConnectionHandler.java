package asembly.httpserver.connection;

import asembly.httpserver.enums.StatusCode;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class ConnectionHandler implements Runnable{

    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);

    protected void sendResponse(Response response, OutputStream output)
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

        try{
            output.write(sb.toString().getBytes(StandardCharsets.UTF_8));

            if(response.getBody() != null && response.getBody().length > 0)
            {
                output.write(response.getBody());
            }

            output.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    protected void sendRequest(Request clientRequest, OutputStream upstreamOutput)
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
