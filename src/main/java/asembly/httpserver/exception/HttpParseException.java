package asembly.httpserver.exception;

import java.io.IOException;

public class HttpParseException extends IOException {
    public HttpParseException(String message) {
        super(message);
    }
}
