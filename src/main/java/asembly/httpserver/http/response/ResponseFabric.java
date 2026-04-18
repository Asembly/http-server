package asembly.httpserver.http.response;

import asembly.httpserver.http.Response;

public class ResponseFabric {

    private final static String version = "HTTP/1.1";

    public static Response of(byte[] body, int status, String contentType)
    {
        return new Response.Builder()
                .body(body)
                .statusCode(status)
                .version(version)
                .addHeader("Content-Length", String.valueOf(body.length))
                .addHeader("Content-Type", contentType)
                .build();
    }
}
