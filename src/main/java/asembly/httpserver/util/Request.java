package asembly.httpserver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private static final Logger log = LoggerFactory.getLogger(Request.class);

    private final Map<String, String> headers;
    private final Map<String, String> params;

    private final String path;
    private final String method;
    private final String version;
    private final String boundary;

    private final byte[] body;

    private Request(Map<String, String> headers, Map<String, String> params, String path, String method, String version, String boundary, byte[] body)
    {
        this.headers = headers;
        this.params = params;
        this.path = path;
        this.method = method;
        this.version = version;
        this.boundary = boundary;
        this.body = body;
    }

    public static String getPath(String path)
    {
        int queryStart = path.indexOf('?');
        return queryStart == -1 ? path : path.substring(0,queryStart);
    }

    public byte[] getBody()
    {
        return body;
    }

    public String getMethod()
    {
        return method;
    }

    public String getVersion() {
        return version;
    }

    public String getBoundary()
    {
        return boundary;
    }

    public String getPath() {
        return path;
    }

    public String getParam(String key)
    {
        return params.getOrDefault(key, "");
    }

    public String getHeader(String key)
    {
        return headers.getOrDefault(key, "");
    }

    public static class Builder
    {
        private final Map<String, String> headers;
        private final Map<String, String> params;

        private byte[] body;

        private String path;
        private String method;
        private String version;
        private String boundary;

        public Builder() {
            this.headers = new HashMap<>();
            this.params = new HashMap<>();
        }

        public Builder path(String path)
        {
            this.path = path;
            return this;
        }

        public Builder method(String method)
        {
            this.method = method;
            return this;
        }

        public Builder version(String version)
        {
            this.version = version;
            return this;
        }

        public Builder boundary(String boundary)
        {
            this.boundary = boundary;
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

        public Builder addHeader(Map<String, String> map)
        {
            this.headers.putAll(map);
            return this;
        }

        public Builder addParam(String key, String value)
        {
            this.params.put(key, value);
            return this;
        }

        public Builder addParam(Map<String, String> map)
        {
            this.params.putAll(map);
            return this;
        }

        public String getHeader(String key)
        {
            return headers.getOrDefault(key, "");
        }

        public Request build()
        {
            return new Request(headers, params, path, method, version, boundary, body);
        }

    }
}
