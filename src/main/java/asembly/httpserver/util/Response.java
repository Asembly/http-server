package asembly.httpserver.util;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Response {

    private final Map<String, String> headers;
    private final String body;
    private final int statusCode;

    private final OutputStream outputStream;

    private Response(Map<String, String> headers, String body, int statusCode, OutputStream outputStream){
        this.headers = headers;
        this.body = body;
        this.statusCode = statusCode;
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream()
    {
        return outputStream;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody()
    {
        return body;
    }

    public Map<String, String> getHeaders()
    {
        return  headers;
    }

    public static class Builder
    {
        private Map<String, String> headers;
        private OutputStream outputStream;

        private String body;

        private int statusCode;

        public Builder(OutputStream outputStream)
        {
            headers = new HashMap<>();
            headers.put("Connection", "close");
            this.outputStream = outputStream;
        }

        public Builder contentType(String contentType)
        {
            headers.put("Content-Type", contentType);
            return this;
        }

        public Builder statusCode(int statusCode)
        {
            this.statusCode = statusCode;
            return this;
        }

        public Builder body(String body)
        {
            this.body = body;
            headers.put("Content-Length", String.valueOf(body.length()));
            return this;
        }

        public Builder addHeader(String key, String value)
        {
            this.headers.put(key, value);
            return this;
        }

        public Response build()
        {
            return new Response(headers, body, statusCode, outputStream);
        }

    }

}
