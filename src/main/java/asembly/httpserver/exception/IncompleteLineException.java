package asembly.httpserver.exception;

public class NotCompleteException extends HttpParseException {
    public NotCompleteException(String message) {
        super(message);
    }
}
