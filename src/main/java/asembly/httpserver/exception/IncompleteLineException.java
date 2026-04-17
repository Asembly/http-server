package asembly.httpserver.exception;

public class IncompleteLineException extends HttpParseException {
    public IncompleteLineException() {
        super("Incomplete line");
    }
}
