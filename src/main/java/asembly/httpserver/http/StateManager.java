package asembly.httpserver.http;

import asembly.httpserver.exception.BalancerNotFoundException;
import asembly.httpserver.exception.ClientCloseException;
import asembly.httpserver.exception.HttpParseException;
import asembly.httpserver.exception.IncompleteLineException;
import asembly.httpserver.http.handler.RouteDispatcher;
import asembly.httpserver.http.io.RequestParser;
import asembly.httpserver.http.io.ResponseParser;
import asembly.httpserver.http.response.JsonResponseService;
import asembly.httpserver.http.response.ResponseSerializer;
import asembly.httpserver.state.ChannelState;
import asembly.httpserver.state.ClientState;
import asembly.httpserver.state.ProxyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class StateManager {

    private static final Logger log = LoggerFactory.getLogger(StateManager.class);

    private final RequestParser requestParser;
    private final ResponseParser responseParser;
    private final RouteDispatcher dispatcher;

    public StateManager()
    {
        this.requestParser = new RequestParser();
        this.responseParser = new ResponseParser();
        this.dispatcher = new RouteDispatcher();
    }

    public void onReadable(SelectionKey key) throws IOException {

        SocketChannel client = (SocketChannel) key.channel();
        ChannelState state = (ChannelState) key.attachment();

        try {
            if(key.attachment() instanceof ClientState)
            {
                requestParser.parse(key);
                var request = state.getRequest();
                if (request != null) {
                    dispatcher.handle(request, (ClientState) state, key);

                    if (state.getResponse() != null)
                    {
                        var responseData = ResponseSerializer.toByteBuffer(state.getResponse());

                        state.setOutput(responseData);

                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                    else
                    {
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
            else if(key.attachment() instanceof ProxyState)
            {

                SocketChannel upstream = (SocketChannel) key.channel();
                var upstreamState = (ProxyState) state;

                responseParser.parse(key);

                var response = upstreamState.getResponse();

                if (response != null) {
                    SelectionKey clientKey = upstreamState.getClient().keyFor(key.selector());

                    var responseData = ResponseSerializer.toByteBuffer(response);

                    upstreamState.getClientState().setOutput(responseData);

                    if (clientKey != null && clientKey.isValid())
                        clientKey.interestOps(SelectionKey.OP_WRITE);
                }

                key.cancel();
                upstream.close();
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
        catch (HttpParseException | BalancerNotFoundException e) {
            var path = state.getRequest() == null ? null : state.getRequest().getPath();
            var response = JsonResponseService.badRequest(e.getMessage(), path);
            var responseData = ResponseSerializer.toByteBuffer(response);
            state.setOutput(responseData);
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    public void onWritable(SelectionKey key) {

        SocketChannel client = (SocketChannel) key.channel();
        ChannelState state = (ChannelState) key.attachment();
        var output = state.getOutput();

        try{
            client.write(output);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        if(output.hasRemaining())
        {
            key.interestOps(SelectionKey.OP_WRITE);
        }
        else {
            state.reset();
            key.interestOps(SelectionKey.OP_READ);
        }
    }


}
