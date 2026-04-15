package asembly.httpserver.http.handler.proxy;

import asembly.httpserver.HttpServer;

import java.util.HashMap;
import java.util.Map;

public class ProxyService {

    private final Map<String, LoadBalancer> balancers = new HashMap<>();

    public ProxyService()
    {
        for(var item: HttpServer.config.getProxyUpstreams().entrySet())
            balancers.put(item.getKey(), new RoundRobinLoadBalancer(item.getValue()));
    }

    public LoadBalancer getBalancer(String serviceName)
    {
        return balancers.get(serviceName);
    }

}
