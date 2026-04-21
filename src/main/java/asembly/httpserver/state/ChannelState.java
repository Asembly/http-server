package asembly.httpserver.state;

import asembly.httpserver.enums.ParsingState;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ChannelState {

    private final ByteBuffer input;
    private final List<String> startLine;

    private Request request;
    private Response response;

    private ByteBuffer output;

    private byte[] body;

    private final Map<String, String> headers;

    private ParsingState parsingState = ParsingState.START_LINE;

    public static long MAX_BODY_SIZE = 1_048_576;

    public ChannelState(ByteBuffer input, ByteBuffer output) {
        this.input = input;
        this.output = output;

        this.startLine = new ArrayList<>();
        this.headers = new HashMap<>();
    }

    public ByteBuffer getInput()  { return input; }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public List<String> getStartLine() {
        return startLine;
    }

    public void addHeader(Map.Entry<String, String> entry)
    {
        this.headers.put(entry.getKey(), entry.getValue());
    }

    public void setParsingState(ParsingState parsingState) { this.parsingState = parsingState; }

    public ParsingState getParsingState() { return parsingState; }

    public Response getResponse() { return response; }
    public Request getRequest() { return request; }

    public void setResponse(Response response) { this.response = response; }
    public void setRequest(Request request) { this.request = request; }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public ByteBuffer getOutput() {
        return output;
    }

    public void setOutput(ByteBuffer output) {
        this.output = output;
    }
}
