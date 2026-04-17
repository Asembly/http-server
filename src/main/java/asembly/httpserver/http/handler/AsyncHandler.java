package asembly.httpserver.http.handler;

import asembly.httpserver.http.Request;

import java.nio.channels.SelectionKey;

public interface AsyncHandler {
    void handle(Request request, SelectionKey clientKey);
}
