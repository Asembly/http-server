package asembly.httpserver.http.handler;

import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;

@FunctionalInterface
public interface SyncHandler {

    Response handle(Request request);

}
