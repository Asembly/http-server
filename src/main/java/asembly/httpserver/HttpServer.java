package asembly.httpserver;

import asembly.httpserver.config.Config;
import asembly.httpserver.http.StateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    public static Config config;
    private static int idx = 0;

    private final InetSocketAddress address;
    private final List<SelectorWorker> workers;
    private final StateManager stateManager;

    public HttpServer(Config config) throws UnknownHostException {
        this.config = config;
        this.address = new InetSocketAddress(
                config.server.host(),
                config.server.port()
        );
        this.workers = new ArrayList<>();
        this.stateManager = new StateManager();
    }

    public void start() throws IOException {

        int n = config.server.threads();
        for (int i = 0; i < n; i++) {
            SelectorWorker w = new SelectorWorker("worker - " + i, stateManager);
            workers.add(w);
        }

        try (var server = ServerSocketChannel.open()) {
            log.info("Server started {}:{}", address.getAddress(), address.getPort());

            var selector = Selector.open();

            server.configureBlocking(false);
            server.bind(address);
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
                            var worker = workers.get(idx);
                            idx = (idx + 1) % workers.size();
                            worker.register(client);
                        }
                    }
                }
            }
        }
    }
}
