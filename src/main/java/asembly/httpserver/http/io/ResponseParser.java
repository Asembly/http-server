package asembly.httpserver.http.io;

import asembly.httpserver.http.Response;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.List;

public class ResponseReader implements HttpParser{

    private final Response.Builder builder;

    public ResponseReader()
    {
        this.builder = new Response.Builder();
    }

    @Override
    public void parse(SelectionKey key) throws HttpParseException, IOException {

    }

    @Override
    public List<String> parseStartLine(String line) {
        return HttpParser.super.parseStartLine(line);
    }
}
