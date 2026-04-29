package asembly.httpserver;

import asembly.httpserver.http.StateManager;
import asembly.httpserver.http.response.JsonResponseService;
import asembly.httpserver.http.response.ResponseSerializer;
import asembly.httpserver.state.ClientState;
import asembly.httpserver.state.ProxyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SelectorWorker {

    private static final Logger log = LoggerFactory.getLogger(SelectorWorker.class);
    private final Thread thread;
    private final Selector selector;
    private final StateManager stateManager;

    public SelectorWorker(String name, StateManager stateManager) throws IOException {
        this.thread = new Thread(this::run, name);
        this.selector = Selector.open();
        this.thread.start();
        this.stateManager = stateManager;
    }

    public void register(SocketChannel channel) throws IOException {
        channel.configureBlocking(false);
        log.info("Client connected: {}", channel.getRemoteAddress());
        log.info("Current worker: {}",thread.getName());
        channel.register(selector, SelectionKey.OP_READ, new ClientState());
        selector.wakeup();
    }

    public void run(){
        try {
            while (true) {
                selector.select();

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if(!key.isValid())
                        continue;

                    try {
                        if (key.isReadable()) {
                            stateManager.onReadable(key);
                        } else if (key.isWritable()) {
                            stateManager.onWritable(key);
                        } else if (key.isConnectable()) {
                            isConnectable(key);
                        }
                    } catch (IOException e) {
                        key.cancel();
                        try {
                            key.channel().close();
                        } catch (IOException ignore) {}
                    }
                }
            }
        }
        catch (IOException e) {
            log.error(e.getMessage());
            log.error("StackTrace: {}", e.getStackTrace());

        }
    }

    private void isConnectable(SelectionKey key)
    {
        SocketChannel upstream = (SocketChannel) key.channel();
        try {
            if (upstream.finishConnect()) {
                key.interestOps(SelectionKey.OP_WRITE);
            } else {
                key.interestOps(SelectionKey.OP_CONNECT);
            }
        }
        catch (IOException e) {
            ProxyState state = (ProxyState) key.attachment();

            SocketChannel client = state.getClient();
            ClientState clientState = state.getClientState();

            var response = JsonResponseService.badGateway(e.getMessage(),
                    clientState.getRequest().getPath());
            var responseData = ResponseSerializer.toByteBuffer(response);
            clientState.setOutput(responseData);
            client.keyFor(key.selector()).interestOps(SelectionKey.OP_WRITE);
        }
    }

}
