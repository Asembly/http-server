package asembly.httpserver.http;

import asembly.httpserver.entity.ClientState;
import asembly.httpserver.enums.ParsingState;
import asembly.httpserver.exception.ClientCloseException;
import asembly.httpserver.exception.HttpParseException;
import asembly.httpserver.exception.IncompleteLineException;
import asembly.httpserver.http.handler.RouteDispatcher;
import asembly.httpserver.http.io.RequestParser;
import asembly.httpserver.http.response.JsonResponseService;
import asembly.httpserver.http.response.ResponseSerializer;
import asembly.httpserver.service.ProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

        try {
            requestParser.parse(key);

            var request = state.getRequest();

            if (request != null) {
                dispatcher.handle(request, state, proxyService);

                if (state.getResponse() == null)
                    throw new IllegalStateException("Response is null");

                var responseData = ResponseSerializer.toByteBuffer(state.getResponse());

                state.setOutput(responseData);

                key.interestOps(SelectionKey.OP_WRITE);
            }
        }
        catch (IncompleteLineException e) {
           key.interestOps(SelectionKey.OP_READ);
        }
        catch (ClientCloseException e)
        {
            client.close();
            key.cancel();
        }
        catch (HttpParseException e) {
            var response = JsonResponseService.badRequest(e.getMessage(), null);
            var responseData = ResponseSerializer.toByteBuffer(response);
            state.setOutput(responseData);
            key.interestOps(SelectionKey.OP_WRITE);
        }
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
