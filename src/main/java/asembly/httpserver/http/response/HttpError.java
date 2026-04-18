package asembly.httpserver.http.response;

public record HttpError(
        int status,
        String error,
        String message,
        String path
) {
}
