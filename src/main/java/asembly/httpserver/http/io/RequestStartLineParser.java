package asembly.httpserver.http.io;

import asembly.httpserver.exception.HttpParseException;

import java.util.List;

public class RequestStartLineParser implements StartLineParser{

    @Override
    public List<String> parse(String line) throws HttpParseException {
        String[] parts = line.split(" ", 3);
        if (parts.length != 3) {
            throw new HttpParseException("Invalid request line: " + line);
        }
        return List.of(parts[0], parts[1], parts[2]);
    }

}
