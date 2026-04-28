package asembly.httpserver.http.handler.proxy;

import asembly.httpserver.config.entity.Path;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLB implements LoadBalancer{

    private final List<Path> upstreams;
    private final AtomicInteger index = new AtomicInteger(0);

    public RoundRobinLB(List<Path> upstreams)
    {
        if(upstreams == null || upstreams.isEmpty())
            throw new IllegalArgumentException("Upstreams list cannot be null or empty");
        this.upstreams = List.copyOf(upstreams);
    }

    @Override
    public Path choose() {

        int i = Math.floorMod(index.getAndIncrement(), upstreams.size());
        Path path = upstreams.get(i);
        return path;
    }
}
