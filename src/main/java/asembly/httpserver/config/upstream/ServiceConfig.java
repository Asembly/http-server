package asembly.httpserver.config.upstream;

import asembly.httpserver.config.PathConfig;

import java.util.List;

public record ServiceConfig(String balancer, List<PathConfig> routes) {
}
