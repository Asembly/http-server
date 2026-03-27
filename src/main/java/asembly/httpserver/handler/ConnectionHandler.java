package asembly.httpserver.handler;

import asembly.httpserver.util.Response;

import java.io.OutputStream;

public interface ConnectionHandler extends Runnable{

    void run();
    void send(Response response, OutputStream output);

}
