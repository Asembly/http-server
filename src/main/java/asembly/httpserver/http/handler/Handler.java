package asembly.httpserver.http.handler;

import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface Handler {

    boolean isAsync();

    Response handleSync(Request request) throws IOException;

    void handleAsync(Request request, SelectionKey clientKey) throws IOException;
}
