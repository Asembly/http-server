package asembly.httpserver.config.entity;

import java.util.List;

public record Service(String balancer, List<Path> routes) {
}
