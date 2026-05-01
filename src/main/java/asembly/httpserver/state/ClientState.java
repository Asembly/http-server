package asembly.httpserver.state;

import asembly.httpserver.enums.ParsingState;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientState {

    public final static long MAX_BODY_SIZE = 1_048_576;

    private final ByteBuffer input;
    private final List<String> startLine;
    private final Map<String, String> headers;

    private Request request;
    private Response response;
    private ByteBuffer output;

    private ParsingState parsingState = ParsingState.START_LINE;
    private FileTransferState fileState;

    private byte[] body;

    public ClientState() {
        this.input = ByteBuffer.allocate(8192);
        this.output = null;

        this.startLine = new ArrayList<>();
        this.headers = new HashMap<>();
    }

    //getter
    public ByteBuffer getInput()  { return input; }
    public Map<String, String> getHeaders() {
        return headers;
    }
    public List<String> getStartLine() {
        return startLine;
    }
    public ParsingState getParsingState() { return parsingState; }
    public byte[] getBody() {
        return body;
    }
    public Response getResponse() { return response; }
    public Request getRequest() { return request; }
    public ByteBuffer getOutput() {
        return output;
    }

    //setter
    public void setResponse(Response response) { this.response = response; }
    public void setRequest(Request request) { this.request = request; }
    public void setBody(byte[] body) {
        this.body = body;
    }
    public void setOutput(ByteBuffer output) {
        this.output = output;
    }
    public void setParsingState(ParsingState parsingState) { this.parsingState = parsingState; }
    public void addHeader(Map.Entry<String, String> entry)
    {
        this.headers.put(entry.getKey(), entry.getValue());
    }
    public void setFileState(FileTransferState fileState) {
        this.fileState = fileState;
    }
    public FileTransferState getFileState() {
        return fileState;
    }


    //reset current state
    public void reset()
    {
        this.setResponse(null);
        this.setRequest(null);
        this.setBody(null);
        this.setOutput(null);
        this.setFileState(null);

        if(this.getInput() != null)
            this.getInput().clear();
        if(this.getOutput() != null)
            this.getOutput().clear();

        this.getStartLine().clear();
        this.setParsingState(ParsingState.START_LINE);
        this.getHeaders().clear();
    }
}
