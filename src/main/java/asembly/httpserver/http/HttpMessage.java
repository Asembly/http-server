package asembly.httpserver.http;

import java.util.HashMap;
import java.util.Map;

public abstract class HttpMessage {

    private final Map<String, String> headers;
    private final byte[] body;
    private final String boundary;
    private final String version;

    protected HttpMessage(Map<String, String> headers, byte[] body, String boundary, String version)
    {
        this.headers = headers;
        this.body = body;
        this.boundary = boundary;
        this.version = version;
    }

    public String getBoundary()
    {
        return boundary;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public byte[] getBody()
    {
        return body;
    }

    public String getVersion()
    {
        return version;
    }

    public static abstract class Builder<B extends Builder<B, M>, M extends HttpMessage>
    {
        protected final Map<String, String> headers = new HashMap<>();
        protected String boundary;
        protected byte[] body;
        protected String version;

        public B body(byte[] body)
        {
            this.body = body;
            return self();
        }

        public B addHeader(String key, String value)
        {
            this.headers.put(key, value);
            return self();
        }

        public B version(String version)
        {
            this.version = version;
            return self();
        }

        public B boundary(String boundary)
        {
            this.boundary = boundary;
            return self();
        }

        public B addHeaders(Map<String, String> map)
        {
            this.headers.putAll(map);
            return self();
        }

        public String getHeader(String key)
        {
           return headers.getOrDefault(key, "");
        }

        protected abstract B self();

        public abstract M build();
    }
}
