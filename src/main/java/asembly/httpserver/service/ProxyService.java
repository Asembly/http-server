package asembly.httpserver.service;

import asembly.httpserver.HttpServer;
import asembly.httpserver.exception.BalancerNotFoundException;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.RequestSerializer;
import asembly.httpserver.http.handler.proxy.LoadBalancer;
import asembly.httpserver.http.handler.proxy.RoundRobinLB;
import asembly.httpserver.state.ClientState;
import asembly.httpserver.state.ProxyState;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class ProxyService {

    private final Map<String, LoadBalancer> balancers = new HashMap<>();

    public ProxyService()
    {
        for(var item: HttpServer.config.getProxyUpstreams().entrySet())
            balancers.put(item.getKey(), new RoundRobinLB(item.getValue()));
    }

    public LoadBalancer getBalancer(String serviceName) throws BalancerNotFoundException {
        var balancer = balancers.get(serviceName);
        if(balancer == null)
            throw new BalancerNotFoundException("Balancer with this service name: " + serviceName + " not found");
        return balancer;
    }

    public void proxy(Request request, URI route, SelectionKey key)
    {
        try{
            if (route != null) {
                InetSocketAddress upstreamAddress = new InetSocketAddress(route.getHost(), route.getPort());
                    var buffer = RequestSerializer.toByteBuffer(request);

                    SocketChannel upstream = SocketChannel.open();
                    upstream.configureBlocking(false);
                    upstream.connect(upstreamAddress);
                    var upstreamState = new ProxyState(buffer, (ClientState) key.attachment(), (SocketChannel) key.channel());
                    upstream.register(key.selector(), SelectionKey.OP_CONNECT, upstreamState);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
