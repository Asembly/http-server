package asembly.httpserver.exception;

import java.io.IOException;

public class ResourceNotFoundException extends IOException {
    public ResourceNotFoundException() {
        super("Resource not found");
    }
}
