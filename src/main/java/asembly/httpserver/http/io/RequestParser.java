package asembly.httpserver.http.io;

import asembly.httpserver.entity.ClientState;
import asembly.httpserver.enums.ParsingState;
import asembly.httpserver.http.HttpParser;
import asembly.httpserver.http.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RequestParser {

    private static final Logger log = LoggerFactory.getLogger(RequestParser.class);
    private final RequestReader reader;
    private final HttpParser parser;

    public RequestParser()
    {
       this.reader = new RequestReader();
       this.parser = new HttpParser();
    }

    public Request parse(ByteBuffer buffer, ClientState state)
    {
        try{
            while(true) {
                switch (state.getParsingState()) {
                    case START_LINE: {

                        byte[] lineBytes = reader.readLine(buffer);

                        if(lineBytes == null)
                            throw new RequestParseException("Request line not completed");

                        String line = new String(lineBytes, StandardCharsets.UTF_8);
                        var startLine = parser.parseStartLine(line);

                        state.getStartLine().addAll(startLine);
                        state.setParsingState(ParsingState.HEADERS);
                        break;
                    }
                    case HEADERS: {
                        byte[] lineBytes = reader.readLine(buffer);

                        if(lineBytes == null)
                            throw new RequestParseException("Request line not completed");

                        String line = new String(lineBytes, StandardCharsets.UTF_8);


                        if(line.isEmpty()) {
                            int contentLength = Integer.parseInt(
                                    state.getHeaders().getOrDefault("Content-Length", "0")
                            );

                            if (contentLength > 0) {
                                state.setParsingState(ParsingState.BODY);
                            } else {
                                state.setParsingState(ParsingState.FINISH);
                            }
                            break;
                        }

                        var header = parser.parseHeader(line);
                        state.addHeader(header);
                        break;
                    }
                    case BODY: {
                        long expected = Long.parseLong(
                                state.getHeaders().getOrDefault("Content-Length", "0")
                        );

                        if (expected < 0 || expected > ClientState.MAX_BODY_SIZE) {
                            throw new RequestParseException("Content-Length out of range");
                        }

                        int remaining = buffer.remaining();

                        if(remaining >= expected)
                        {
                            byte[] bodyBytes = new byte[(int) expected];
                            buffer.get(bodyBytes);
                            state.setBody(bodyBytes);
                            state.setParsingState(ParsingState.FINISH);
                        }
                        else {
                            log.warn("Incomplete body: received {} of {} bytes", remaining, expected);
                            throw new RequestParseException(
                                    String.format("Incomplete body: received %d of %d bytes", remaining, expected)
                            );
                        }
                    }
                    case FINISH: {
                        var requestBuilder = new Request.Builder();
                        requestBuilder.path(state.getStartLine().get(1));
                        requestBuilder.version(state.getStartLine().getLast());
                        requestBuilder.method(state.getStartLine().getFirst());
                        requestBuilder.addHeaders(state.getHeaders());
                        requestBuilder.body(state.getBody());

                        return requestBuilder.build();
                    }
                }
            }
        }
        catch (RequestParseException e){
            return null;
        }
    }
}
