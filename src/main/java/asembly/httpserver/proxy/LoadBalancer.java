package asembly.httpserver.proxy;

import java.net.URI;

public interface LoadBalancer {

    URI choose();

}
