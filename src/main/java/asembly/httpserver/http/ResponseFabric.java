package asembly.httpserver.http;

public class ResponseFabric {

    public static Response ok(byte[] body, String contentType)
    {
        return new Response.Builder()
                .body(body)
                .statusCode(200)
                .version("HTTP/1.1")
                .addHeader("Content-Length", String.valueOf(body.length))
                .addHeader("Content-Type", contentType)
                .build();
    }

    public static Response notFound()
    {
        var body = "<h1>Resource not found</h1>".getBytes();
        return new Response.Builder()
               .body(body)
               .statusCode(404)
                .version("HTTP/1.1")
               .addHeader("Content-Length", String.valueOf(body.length))
               .addHeader("Content-Type", "text/html")
               .build();
    }

    public static Response badRequest(byte[] body)
    {
        return new Response.Builder()
                .body(body)
                .statusCode(400)
                .addHeader("Content-Length", String.valueOf(body.length))
                .addHeader("Content-Type", "application/json")
                .build();
    }

    public static Response internalError(byte[] body)
    {
        return new Response.Builder()
                .body(body)
                .statusCode(500)
                .addHeader("Content-Length", String.valueOf(body.length))
                .addHeader("Content-Type", "application/json")
                .build();
    }

    public static Response forbidden(byte[] body)
    {
        return new Response.Builder()
                .body(body)
                .statusCode(403)
                .addHeader("Content-Length", String.valueOf(body.length))
                .addHeader("Content-Type", "application/json")
                .build();
    }

}
