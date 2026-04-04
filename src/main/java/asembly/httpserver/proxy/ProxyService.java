package asembly.httpserver.proxy;

import asembly.httpserver.HttpServer;
import asembly.httpserver.config.ServerConfig;

import java.util.HashMap;
import java.util.Map;

public class ProxyService {

    private final Map<String, LoadBalancer> balancers = new HashMap<>();

    public ProxyService(ServerConfig config)
    {
        for(var item: HttpServer.getConfig().getProxyUpstreams().entrySet())
            balancers.put(item.getKey(), new RoundRobinLoadBalancer(item.getValue()));
    }

    public LoadBalancer getBalancer(String serviceName)
    {
        return balancers.get(serviceName);
    }

}
