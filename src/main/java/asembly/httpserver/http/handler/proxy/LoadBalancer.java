package asembly.httpserver.http.handler.proxy;

import asembly.httpserver.config.entity.PathConfig;

public interface LoadBalancer {
    PathConfig choose();
}
