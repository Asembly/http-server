package asembly.httpserver.http.handler.proxy;

import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer{

    private final List<URI> upstreams;
    private final AtomicInteger index = new AtomicInteger(0);

    public RoundRobinLoadBalancer(List<URI> upstreams)
    {
        if(upstreams == null || upstreams.isEmpty())
            throw new IllegalArgumentException("Upstreams list cannot be null or empty");
        this.upstreams = List.copyOf(upstreams);
    }

    @Override
    public URI choose() {

        int i = Math.floorMod(index.getAndIncrement(), upstreams.size());
        return upstreams.get(i);
    }
}
