package asembly.httpserver.handler;

import asembly.httpserver.util.Request;

import java.io.OutputStream;

@FunctionalInterface
public interface Handler {

    void handle(Request request, OutputStream outputStream);

}
