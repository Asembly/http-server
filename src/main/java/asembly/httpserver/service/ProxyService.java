package asembly.httpserver.service;

import asembly.httpserver.HttpServer;
import asembly.httpserver.config.PathConfig;
import asembly.httpserver.enums.LoadBalancerType;
import asembly.httpserver.exception.InternalException;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.serialize.RequestSerializer;
import asembly.httpserver.http.handler.proxy.LoadBalancer;
import asembly.httpserver.http.handler.proxy.LoadBalancerFactory;
import asembly.httpserver.http.state.ClientState;
import asembly.httpserver.http.state.ProxyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class ProxyService {

    private static final Logger log = LoggerFactory.getLogger(ProxyService.class);
    private final Map<String, LoadBalancer> balancers = new HashMap<>();

    public ProxyService()
    {
        for(var item: HttpServer.config.upstream.entrySet()) {
            balancers.put(item.getKey(), LoadBalancerFactory.create(
                    LoadBalancerType.fromString(item.getValue().balancer()),
                    item.getValue().routes())
            );
        }
    }

    public LoadBalancer getBalancer(String serviceName) throws InternalException {
        var balancer = balancers.get(serviceName);
        if(balancer == null)
            throw new InternalException("Balancer with this service name: " + serviceName + " not found");
        return balancer;
    }

    public boolean ping(String host, int port, int timeoutMillis)
    {
        try(Socket socket = new Socket())
        {
            socket.connect(new InetSocketAddress(host, port), timeoutMillis);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void proxy(Request request, PathConfig route, SelectionKey key)
    {
        try{
            if (route != null) {
                InetSocketAddress upstreamAddress = new InetSocketAddress(route.host(), route.port());
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
