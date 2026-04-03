package asembly.httpserver.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String key) {
        super("Resource not found: " + key);
    }
}
