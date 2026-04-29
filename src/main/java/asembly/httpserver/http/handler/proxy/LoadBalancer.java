package asembly.httpserver.http.handler.proxy;

import asembly.httpserver.config.entity.Path;

public interface LoadBalancer {
    Path choose();
}
