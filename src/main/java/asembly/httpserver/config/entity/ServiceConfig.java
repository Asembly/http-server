package asembly.httpserver.config.entity;

import java.util.List;

public record ServiceConfig(String balancer, List<PathConfig> routes) {
}
