package asembly.httpserver.http.handler.proxy;

import asembly.httpserver.config.PathConfig;

public interface LoadBalancer {
    PathConfig choose();
}
