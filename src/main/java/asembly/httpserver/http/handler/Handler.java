package asembly.httpserver.http.handler;

import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;

@FunctionalInterface
public interface Handler {

    Response handle(Request request);

}
