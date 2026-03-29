package asembly.httpserver.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpParser {

    public Map<String, String> parseHeader(String line) throws IOException {
        Map<String, String> headers = new HashMap<>();

        int hdIndex = line.indexOf(':');

        String key = line.substring(0, hdIndex);
        String value = line.substring(hdIndex+2);

        headers.put(key, value);
        return headers;
    }

    public List<String> parseStartLine(String line)
    {
        List<String> list = new ArrayList<>();
        list.add(line.split(" ")[0]);
        list.add(line.split(" ")[1]);
        list.add(line.split(" ")[2]);
        return list;
    }

    public byte[] parseBody(InputStream input, int contentLength) throws IOException {
        byte[] bodyChars = new byte[contentLength];
        int read = 0;

        while(read < contentLength)
        {
            int r = input.read(bodyChars, read, contentLength - read);
            if(r == -1)
                break;
            read += r;
        }

        return bodyChars;
    }

    public Map<String, String> parseParam(String path)
    {
        Map<String, String> params = new HashMap<>();

        int queryStart = path.indexOf('?');
        if(queryStart == -1) return params;

        String query = path.substring(queryStart+1);

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
}
