package asembly.httpserver.http.io;

import asembly.httpserver.enums.ParsingState;
import asembly.httpserver.exception.HttpParseException;
import asembly.httpserver.exception.IncompleteLineException;
import asembly.httpserver.state.ClientState;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpMessageParser{

    private final LineReader lineReader;
    private final StartLineParser startLineParser;

    public HttpMessageParser(StartLineParser startLineParser)
    {
        this.startLineParser = startLineParser;
        lineReader = new LineReader();
    }

    public void parse(ByteBuffer buffer, ClientState state) throws HttpParseException{

        if (state.getParsingState() == ParsingState.START_LINE) {
            parseStartLine(buffer, state, startLineParser);
        }

        if (state.getParsingState() == ParsingState.HEADERS) {
            parseHeaders(buffer, state);
        }

        if (state.getParsingState() == ParsingState.BODY) {
            parseBody(buffer, state);
        }
    }

    private void parseStartLine(ByteBuffer buffer, ClientState state, StartLineParser startLineParser) throws HttpParseException
    {
        byte[] lineBytes = lineReader.readLine(buffer);
        if (lineBytes == null) {
            throw new IncompleteLineException();
        }

        String line = new String(lineBytes, StandardCharsets.UTF_8);
        var startLine = startLineParser.parse(line);

        state.getStartLine().addAll(startLine);
        state.setParsingState(ParsingState.HEADERS);
    }

    private void parseHeaders(ByteBuffer buffer, ClientState state) throws HttpParseException
    {
        byte[] lineBytes = lineReader.readLine(buffer);

        if (lineBytes == null)
            throw new IncompleteLineException();

        String line = new String(lineBytes, StandardCharsets.UTF_8);

        if (line.isEmpty()) {
            int contentLength = Integer.parseInt(
                    state.getHeaders().getOrDefault("Content-Length", "0")
            );

            if (contentLength > 0) {
                state.setParsingState(ParsingState.BODY);
            } else {
                state.setParsingState(ParsingState.FINISH);
            }
        }
        else
        {
            var header = parseHeader(line);
            state.addHeader(header);
        }
    }

    private void parseBody(ByteBuffer buffer, ClientState state) throws HttpParseException {

        long expected = Long.parseLong(
                state.getHeaders().getOrDefault("Content-Length", "0")
        );

        if (expected < 0 || expected > ClientState.MAX_BODY_SIZE) {
            throw new HttpParseException("Content-Length out of range");
        }

        int remaining = buffer.remaining();

        if (remaining >= expected) {
            byte[] bodyBytes = new byte[(int) expected];
            buffer.get(bodyBytes);
            state.setBody(bodyBytes);
            state.setParsingState(ParsingState.FINISH);
        }
        else
        {
            throw new IncompleteLineException();
        }
    }

    private Map.Entry<String,String> parseHeader(String line) throws HttpParseException {
        int idx = line.indexOf(':');
        if (idx <= 0) {
            throw new HttpParseException("Invalid header");
        }
        String name = line.substring(0, idx).trim();
        String value = line.substring(idx + 1).trim();
        return Map.entry(name, value);
    }
}
