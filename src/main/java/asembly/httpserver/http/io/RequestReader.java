package asembly.httpserver.http.io;

import asembly.httpserver.http.Request;
import asembly.httpserver.parser.HttpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class RequestReader extends HttpReader{

    private static final Logger log = LoggerFactory.getLogger(RequestReader.class);
    private final Request.Builder builder;
    private final HttpParser parser;

    public RequestReader()
    {
       this.builder = new Request.Builder();
       this.parser = new HttpParser();
    }

    @Override
    public Request read(InputStream input) throws IOException {
        var startLine = parser.parseStartLine(readLine(input));
        var path = startLine.get(1);

        builder.method(startLine.getFirst());
        builder.path(path);
        builder.version(startLine.getLast());
        builder.addParams(parser.parseParam(path));

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
