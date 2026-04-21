package asembly.httpserver.http.handler;

import asembly.httpserver.http.Request;

import java.io.IOException;
import java.nio.channels.SelectionKey;

@FunctionalInterface
public interface AsyncHandler {

    void handle(Request request, SelectionKey clientKey) throws IOException;

}
