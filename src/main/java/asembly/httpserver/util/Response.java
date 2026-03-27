package asembly.httpserver.util;

import java.util.HashMap;
import java.util.Map;

public class Response {

    private final Map<String, String> headers;
    private final byte[] body;
    private final int statusCode;

    private Response(Map<String, String> headers, byte[] body, int statusCode){
        this.headers = headers;
        this.body = body;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public byte[] getBody()
    {
        return body;
    }

    public Map<String, String> getHeaders()
    {
        return  headers;
    }

    public static class Builder
    {
        private final Map<String, String> headers;

        private byte[] body;
        private int statusCode;

        public Builder()
        {
            headers = new HashMap<>();
        }

        public Builder statusCode(int statusCode)
        {
            this.statusCode = statusCode;
            return this;
        }

        public Builder body(byte[] body)
        {
            this.body = body;
            return this;
        }

        public Builder addHeader(String key, String value)
        {
            this.headers.put(key, value);
            return this;
        }

        public Response build()
        {
            return new Response(headers, body, statusCode);
        }
    }

}
