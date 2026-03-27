package org.example.util;

import org.example.parser.RequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class RequestWriter {

    private static final Logger log = LoggerFactory.getLogger(RequestWriter.class);
    private final Request.Builder builder;
    private final RequestParser parser;

    public RequestWriter()
    {
       this.builder = new Request.Builder();
       this.parser = new RequestParser();
    }

    public Request write(InputStream input) throws IOException {
        var startLine = parser.parseStartLine(readLine(input));
        var path = startLine.get(1);
        builder.method(startLine.getFirst());
        builder.path(path);
        builder.version(startLine.getLast());

        builder.addParam(parser.parseParam(path));

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
            builder.addHeader(headers);
        }
        int contentLength = Integer.parseInt(builder.getHeader("Content-Length"));
        var body = parser.parseBody(input, contentLength);
        builder.body(body);
        return builder.build();
    }

    private String readLine(InputStream input) throws IOException {
        StringBuilder builder = new StringBuilder();
        int byteRead;
        while((byteRead = input.read()) != '\n')
        {
            if(byteRead != '\r') builder.append((char)byteRead);
        }
        return builder.toString();
    }
}
