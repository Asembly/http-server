package asembly.httpserver.http.io;

import asembly.httpserver.http.HttpParser;
import asembly.httpserver.http.Response;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ResponseReader implements HttpReader{

    private final Response.Builder builder;
    private final HttpParser parser;

    public ResponseReader()
    {
        this.builder = new Response.Builder();
        this.parser = new HttpParser();
    }

    @Override
    public byte[] readLine(ByteBuffer buffer) throws IOException {
        return null;
    }
}
