package asembly.httpserver.http.handler.proxy;

import asembly.httpserver.config.PathConfig;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLB implements LoadBalancer{

    private final List<PathConfig> upstreams;
    private final AtomicInteger index = new AtomicInteger(0);

    public RoundRobinLB(List<PathConfig> upstreams)
    {
        if(upstreams == null || upstreams.isEmpty())
            throw new IllegalArgumentException("Upstreams list cannot be null or empty");
        this.upstreams = List.copyOf(upstreams);
    }

    @Override
    public PathConfig choose() {

        int i = Math.floorMod(index.getAndIncrement(), upstreams.size());
        PathConfig path = upstreams.get(i);
        return path;
    }
}
