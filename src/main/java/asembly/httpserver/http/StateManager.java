package asembly.httpserver.http;

import asembly.httpserver.entity.ClientState;
import asembly.httpserver.enums.ParsingState;
import asembly.httpserver.http.handler.RouteDispatcher;
import asembly.httpserver.http.handler.proxy.ProxyService;
import asembly.httpserver.http.io.RequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class StateManager {

    private static final Logger log = LoggerFactory.getLogger(StateManager.class);

    private final RequestParser requestParser;
    private final RouteDispatcher dispatcher;
    private final ProxyService proxyService;

    public StateManager()
    {
        this.requestParser = new RequestParser();
        this.dispatcher = new RouteDispatcher();
        this.proxyService = new ProxyService();
    }

    public void onReadable(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();

        ByteBuffer buffer = state.getInput();

        int n = client.read(buffer);

        if (n == -1) {
            client.close();
            key.cancel();
            return;
        }

        buffer.flip();

        var request = requestParser.parse(state.getInput(), state);

        state.setRequest(request);

        if (state.getRequest() != null) {
            dispatcher.handle(request,state, proxyService);
            var responseData = ResponseSerializer.toByteBuffer(state.getResponse());

            state.setOutput(responseData);

            var response = state.getResponse();

            if(response == null)
                throw new IllegalStateException("Handler returned null Response");

            key.interestOps(SelectionKey.OP_WRITE);
        }

        buffer.compact();
    }

    public void onWritable(SelectionKey key) throws IOException {

        SocketChannel client = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();
        var output = state.getOutput();

        client.write(output);

        if(output.hasRemaining())
        {
            key.interestOps(SelectionKey.OP_WRITE);
        }
        else {
            refreshState(state);
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private void refreshState(ClientState state)
    {
        state.setResponse(null);
        state.setRequest(null);
        state.setBody(null);
        state.setOutput(null);

        state.getInput().clear();
        state.getStartLine().clear();
        state.setParsingState(ParsingState.START_LINE);
        state.getHeaders().clear();
    }

}
