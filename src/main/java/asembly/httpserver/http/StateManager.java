package asembly.httpserver.http;

import asembly.httpserver.exception.HttpParseException;
import asembly.httpserver.exception.IncompleteLineException;
import asembly.httpserver.exception.InternalException;
import asembly.httpserver.http.handler.RouteDispatcher;
import asembly.httpserver.http.io.RequestParser;
import asembly.httpserver.http.io.ResponseParser;
import asembly.httpserver.http.response.JsonResponseService;
import asembly.httpserver.http.serialize.ResponseSerializer;
import asembly.httpserver.http.state.ClientState;
import asembly.httpserver.http.state.FileTransferState;
import asembly.httpserver.http.state.ProxyState;
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
        ClientState state = (ClientState) key.attachment();

        try {
            if(key.attachment() instanceof ProxyState)
                onProxyState(key, (ProxyState) state);
            else if(key.attachment() instanceof ClientState)
                onClientState(key, state);
        }
        catch (IncompleteLineException e) {
           key.interestOps(SelectionKey.OP_READ);
        }
        catch (HttpParseException e) {
            log.error("HttpParseException: {}\n{}", e.getMessage(), e.getStackTrace());

            var path = state.getRequest() == null ? null : state.getRequest().getPath();
            var response = JsonResponseService.badRequest(e.getMessage(), path);
            var buffer = ResponseSerializer.toByteBuffer(response);
            state.setOutput(buffer);
            key.interestOps(SelectionKey.OP_WRITE);
        }
        catch (InternalException e) {
            log.error("InternalException: {}\n{}", e.getMessage(), e.getStackTrace());

            var response = JsonResponseService.internalError(e.getMessage());
            var buffer = ResponseSerializer.toByteBuffer(response);
            state.setOutput(buffer);
            key.interestOps(SelectionKey.OP_WRITE);
        }
        catch (IOException e) {
            log.warn("Read failed, closing channel {}: {}", client, e.getMessage());

            key.cancel();
            client.close();
        }
    }

    public void onWritable(SelectionKey key) throws IOException {

        SocketChannel client = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();

        var output = state.getOutput();

        try{

            client.write(output);
            if(output.hasRemaining())
            {
                key.interestOps(SelectionKey.OP_WRITE);
                return;
            }

            FileTransferState fs = state.getFileState();
            if(fs != null)
            {
                var transferred = fs.transferTo(client);
                if(transferred == 0 && !fs.finished()) {
                    key.interestOps(SelectionKey.OP_WRITE);
                    return;
                }

                if(fs.finished()) {
                    fs.close();
                    state.reset();
                    key.interestOps(SelectionKey.OP_READ);
                }
                else {
                   key.interestOps(SelectionKey.OP_WRITE);
                }
            }
            else {
                state.reset();
                key.interestOps(SelectionKey.OP_READ);
            }
        }
        catch (IOException e) {
            log.warn("Write failed, closing channel {}: {}", client, e.getMessage());
            key.cancel();
            client.close();
        }
    }

    private void onClientState(SelectionKey key, ClientState state) throws IOException {
        requestParser.parse(key);
        var request = state.getRequest();

        log.debug("Client request:\n{}", request);

        dispatcher.handle(request, (ClientState) state, key);
    }

    private void onProxyState(SelectionKey key, ProxyState state) throws IOException {
        SocketChannel upstream = (SocketChannel) key.channel();

        //Parse proxy response
        responseParser.parse(key);
        var response = state.getResponse();

        log.debug("Proxy response received:\n{}",response);

        //Get the client state to write the proxy response to it
        SelectionKey clientKey = state.getClient().keyFor(key.selector());
        var buffer = ResponseSerializer.toByteBuffer(response);
        state.getClientState().setOutput(buffer);

        if (clientKey != null && clientKey.isValid())
            clientKey.interestOps(SelectionKey.OP_WRITE);
        else
        {
            log.error("Client key {}, is not available", clientKey);
        }

        key.cancel();
        upstream.close();
    }

}
