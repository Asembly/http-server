package asembly.httpserver.http.handler.proxy;

import asembly.httpserver.HttpServer;
import asembly.httpserver.entity.ClientState;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
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

    public void proxy(ByteBuffer request, SelectionKey key)
    {
        SocketChannel client = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();


//        client.write(request);

    }

}
