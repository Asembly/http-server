package asembly.httpserver.http.handler.proxy;

import java.net.URI;

public interface LoadBalancer {

    URI choose();

}
