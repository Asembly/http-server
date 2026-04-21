package asembly.httpserver.http.io;

import asembly.httpserver.enums.ParsingState;
import asembly.httpserver.exception.ClientCloseException;
import asembly.httpserver.http.Response;
import asembly.httpserver.state.ChannelState;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ResponseParser implements HttpParser{

    private final HttpMessageParser parser;

    public ResponseParser()
    {
        this.parser = new HttpMessageParser(new ResponseStartLineParser());
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
                    var responseParser = new Response.Builder();
                    var line = state.getStartLine();
                    responseParser.statusCode(Integer.parseInt(line.get(1)));
                    responseParser.version(line.getFirst());
                    responseParser.addHeaders(state.getHeaders());
                    responseParser.body(state.getBody());

                    var request = responseParser.build();

                    state.setResponse(request);
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
