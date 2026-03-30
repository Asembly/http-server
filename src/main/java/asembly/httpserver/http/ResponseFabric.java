package asembly.httpserver.http;

public class ResponseFabric {

    public static Response ok(byte[] body)
    {
        return new Response.Builder()
                .body(body)
                .statusCode(200)
                .addHeader("Connection", "close")
                .addHeader("Content-Length", String.valueOf(body.length))
                .addHeader("Content-Type", "application/json")
                .build();
    }

    public static Response notFound()
    {
        var body = "not found".getBytes();
        return new Response.Builder()
               .body(body)
               .statusCode(404)
               .addHeader("Connection", "close")
               .addHeader("Content-Length", String.valueOf(body.length))
               .addHeader("Content-Type", "application/json")
               .build();
    }

}
