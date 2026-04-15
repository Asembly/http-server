package asembly.httpserver.http;

import java.util.Map;

public class Response extends HttpMessage {

    private final int statusCode;

    private Response(Map<String, String> headers, byte[] body, int statusCode, String boundary, String version){
        super(headers, body, boundary, version);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static class Builder extends HttpMessage.Builder<Builder, HttpMessage>
    {
        private int statusCode;

        public Builder statusCode(int statusCode)
        {
            this.statusCode = statusCode;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Response build()
        {
            return new Response(headers, body, statusCode, boundary, version);
        }
    }

}
