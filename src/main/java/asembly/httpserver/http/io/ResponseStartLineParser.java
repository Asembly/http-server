package asembly.httpserver.http.io;

import asembly.httpserver.exception.HttpParseException;

import java.util.List;

public class ResponseStartLineParser implements StartLineParser{
    @Override
    public List<String> parse(String line) throws HttpParseException {
        String[] parts = line.split(" ", 3);
        if (parts.length < 2) {
            throw new HttpParseException("Invalid status line: " + line);
        }
        String version = parts[0];
        String statusCode = parts[1];
        String reasonPhrase = parts.length > 2 ? parts[2] : "";
        return List.of(version, statusCode, reasonPhrase);
    }
}
