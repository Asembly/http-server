package asembly.httpserver.http.io;

import asembly.httpserver.exception.HttpParseException;
import asembly.httpserver.http.Response;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public class ResponseParser implements HttpParser{

    private final Response.Builder builder;

    public ResponseParser()
    {
        this.builder = new Response.Builder();
    }

    @Override
    public void parse(SelectionKey key) throws HttpParseException, IOException {

    }
}
