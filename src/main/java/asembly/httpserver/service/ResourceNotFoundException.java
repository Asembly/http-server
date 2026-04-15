package asembly.httpserver.service;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String key) {
        super("Resource not found: " + key);
    }
}
