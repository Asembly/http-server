package asembly.httpserver;

import asembly.httpserver.config.ServerConfig;
import asembly.httpserver.entity.ClientState;
import asembly.httpserver.http.StateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class HttpServer {

    private static final Logger log = LoggerFactory.getLogger(HttpServer.class);
    private final InetAddress address;
    private final int port;

    private final StateManager stateManager;

    public static ServerConfig config;

    public HttpServer(ServerConfig config) throws UnknownHostException {
        this.config = config;
        this.port = config.getPort();
        this.address = InetAddress.getByName(config.getHost());
        this.stateManager = new StateManager();
    }

    public void start() throws IOException {

        try (var server = ServerSocketChannel.open()) {
            log.info("Server started {}:{}", address.getHostAddress(), port);

            var selector = Selector.open();

            server.configureBlocking(false);
            server.bind(new InetSocketAddress(config.getHost(), config.getPort()));
            server.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (key.isAcceptable()) {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel client = serverChannel.accept();
                        if (client != null) {
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ, new ClientState());
                        }
                    }
                    else if (key.isReadable()) {
                        stateManager.onReadable(key);
                    }
                    else if(key.isWritable())
                    {
                        stateManager.onWritable(key);
                    }
                }
            }
        }
    }
}
