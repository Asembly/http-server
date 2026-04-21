package asembly.httpserver.http.io;

import asembly.httpserver.enums.ParsingState;
import asembly.httpserver.exception.ClientCloseException;
import asembly.httpserver.http.Request;
import asembly.httpserver.state.ChannelState;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class RequestParser implements HttpParser{

    private final HttpMessageParser parser;

    public RequestParser()
    {
       this.parser = new HttpMessageParser(new RequestStartLineParser());
    }

    public void parse(SelectionKey key) throws IOException {

        SocketChannel client = (SocketChannel) key.channel();
        ChannelState state = (ChannelState) key.attachment();

        ByteBuffer buffer = state.getInput();
        try {
            int n = client.read(buffer);

            if (n == -1)
                throw new ClientCloseException();

            buffer.flip();

            while (buffer.hasRemaining()) {
                parser.parse(buffer, state);
                if (ParsingState.FINISH.equals(state.getParsingState())) {
                    var requestBuilder = new Request.Builder();
                    requestBuilder.path(state.getStartLine().get(1));
                    requestBuilder.version(state.getStartLine().getLast());
                    requestBuilder.method(state.getStartLine().getFirst());
                    requestBuilder.addHeaders(state.getHeaders());
                    requestBuilder.body(state.getBody());

                    var request = requestBuilder.build();

                    state.setRequest(request);
                    break;
                }
            }

            buffer.compact();
        }
        catch (SocketException e)
        {
            throw new ClientCloseException();
        }
    }
}
