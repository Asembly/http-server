package asembly.httpserver.http.handler.proxy;

import asembly.httpserver.config.entity.Path;
import asembly.httpserver.enums.LoadBalancerType;

import java.util.List;

public class LoadBalancerFactory {
    public static LoadBalancer create(LoadBalancerType type, List<Path> upstreams) {
        return switch (type) {
            case ROUND_ROBIN -> new RoundRobinLB(upstreams);
            case WEIGHTED -> null;
        };
    }
}
