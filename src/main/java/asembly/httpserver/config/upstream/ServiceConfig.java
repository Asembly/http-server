package asembly.httpserver.config.upstream;

import asembly.httpserver.config.PathConfig;

import java.util.List;

public record ServiceConfig(String balancer, List<PathConfig> routes) {

    public static final String DEFAULT_BALANCER = "round_robin";

    public ServiceConfig {
        if (balancer == null || balancer.isBlank()) {
            balancer = DEFAULT_BALANCER;
        }
        routes = List.copyOf(routes);
    }

    public ServiceConfig(List<PathConfig> routes) {
        this(DEFAULT_BALANCER, routes);
    }

}
