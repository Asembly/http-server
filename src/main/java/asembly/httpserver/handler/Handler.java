package asembly.httpserver.handler;

import asembly.httpserver.util.Request;
import asembly.httpserver.util.Response;

@FunctionalInterface
public interface Handler {

    Response handle(Request request);

}
