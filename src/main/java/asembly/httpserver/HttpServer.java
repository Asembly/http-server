package asembly.httpserver;

import asembly.httpserver.config.ServerConfig;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HttpServer {

    private static final Logger log = LoggerFactory.getLogger(HttpServer.class);

    public static ServerConfig config;
    private static int idx = 0;

    private final InetAddress address;
    private final List<SelectorWorker> workers;

    private final int port;

    public HttpServer(ServerConfig config) throws UnknownHostException {
        this.config = config;
        this.port = config.getPort();
        this.address = InetAddress.getByName(config.getHost());
        this.workers = new ArrayList<>();
    }

    public void start() throws IOException {

        int n = config.getThreads();
        for (int i = 0; i < n; i++) {
            SelectorWorker w = new SelectorWorker("worker - " + i);
            workers.add(w);
        }

        try (var server = ServerSocketChannel.open()) {
            log.info("Server started {}:{}", address.getHostAddress(), port);

            var selector = Selector.open();

            server.configureBlocking(false);
            server.bind(new InetSocketAddress(config.getHost(), config.getPort()));
            server.register(selector, SelectionKey.OP_ACCEPT);

            while(true)
            {
                selector.select();
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();

                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if(key.isAcceptable())
                    {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel client = serverChannel.accept();
                        if (client != null) {
                            SelectorWorker w = workers.get(idx);
                            idx = (idx + 1) % workers.size();
                            w.register(client);
                        }
                    }
                }
            }
        }
    }
}
