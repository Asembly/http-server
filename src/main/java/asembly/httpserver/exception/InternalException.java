package asembly.httpserver.exception;

import java.io.IOException;

public class InternalException extends IOException {
    public InternalException(String message) {
        super(message);
    }
}
