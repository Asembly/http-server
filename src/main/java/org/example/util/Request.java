package org.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private static final Logger log = LoggerFactory.getLogger(Request.class);
    private final InputStream inputStream;

    private Map<String, String> headers;
    private Map<String, String> params;

    private String uri;
    private String method;
    private String version;
    private byte[] body;

    public Request(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        parse();
    }

    private Map<String, String> parseParam()
    {
        Map<String, String> params = new HashMap<>();

        int queryStart = uri.indexOf('?');
        if(queryStart == -1) return params;

        String query = uri.substring(queryStart+1);

        for(var param: query.split("&"))
        {
            int eqIndex = param.indexOf('=');
            if(eqIndex > 0)
            {
                String key = param.substring(0, eqIndex);
                String value = param.substring(eqIndex+1);

                params.put(URLDecoder.decode(key, StandardCharsets.UTF_8),
                        URLDecoder.decode(value, StandardCharsets.UTF_8));
            }
        }
        return params;
    }

    public static String getPath(String path)
    {
        int queryStart = path.indexOf('?');
        return queryStart == -1 ? path : path.substring(0,queryStart);
    }

    private Map<String, String> parseHeader() throws IOException {
        Map<String, String> headers = new HashMap<>();

        while(true)
        {
            String line = readLine();

            if(line.isEmpty())
                break;

            int hdIndex = line.indexOf(':');

            String key = line.substring(0, hdIndex);
            String value = line.substring(hdIndex+2);

            headers.put(key, value);
        }
        return headers;
    }

    private byte[] parseBody() throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));

        byte[] bodyChars = new byte[contentLength];
        int read = 0;

        while(read < contentLength)
        {
            int r = inputStream.read(bodyChars, read, contentLength - read);
            if(r == -1)
                break;
            read += r;
        }

        return bodyChars;
    }

    public void parse() throws IOException {

        String line = readLine();
        method = line.split(" ")[0];
        uri = line.split(" ")[1];
        version = line.split(" ")[2];

        params = parseParam();
        headers = parseHeader();
        body = parseBody();
    }

    public String readLine() throws IOException {
        StringBuilder builder = new StringBuilder();
        int byteRead;
        while((byteRead = inputStream.read()) != '\n')
        {
           if(byteRead != '\r') builder.append((char)byteRead);
        }
        return builder.toString();
    }

    public String getMethod()
    {
        return method;
    }

    public byte[] getBody()
    {
        return body;
    }

    public String getVersion() {
        return version;
    }

    public String getUri() {
        return uri;
    }

    public String getParam(String key)
    {
        return params.getOrDefault(key, "");
    }

    public String getHeaders(String key)
    {
        return headers.getOrDefault(key, "");
    }
}
