package asembly.httpserver.util;

import asembly.httpserver.parser.HttpParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ResponseReader extends HttpReader{

    private final Response.Builder builder;
    private final HttpParser parser;

    public ResponseReader()
    {
        this.builder = new Response.Builder();
        this.parser = new HttpParser();
    }

    @Override
    protected Response read(InputStream input) throws IOException {
        var startLine = parser.parseStartLine(readLine(input));
        var version = startLine.get(0);
        var statusCode = startLine.get(1);

        builder.statusCode(Integer.parseInt(statusCode));
        builder.version(version);

        while(true) {
            String line = readLine(input);

            if(line.isEmpty())
                break;

            Map<String, String> headers = parser.parseHeader(line);
            String contentType = headers.getOrDefault("Content-Type", "");

            if(contentType.contains("boundary"))
            {
                var splitLine = contentType.split("; ");
                var boundary = splitLine[1].split("=")[1];
                builder.addHeader("Content-Type",splitLine[0]);
                builder.boundary(boundary);
                continue;
            }
            builder.addHeaders(headers);
        }
        if(!builder.getHeader("Content-Length").isEmpty())
        {
            int contentLength = Integer.parseInt(builder.getHeader("Content-Length"));
            var body = parser.parseBody(input, contentLength);
            builder.body(body);
        }

        return builder.build();
    }
}
