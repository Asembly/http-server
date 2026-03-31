package asembly.httpserver.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Request extends HttpMessage{

    private static final Logger log = LoggerFactory.getLogger(Request.class);

    private final Map<String, String> params;

    private final String path;
    private final String method;

    private Request(Map<String, String> headers, Map<String, String> params, String path, String method, String version, String boundary, byte[] body)
    {
        super(headers, body, boundary, version);
        this.params = params;
        this.path = path;
        this.method = method;
    }

    public String getPathWithoutQuery(String path)
    {
        int queryStart = path.indexOf('?');
        return queryStart == -1 ? path : path.substring(0,queryStart);
    }

    public String getBasePath()
    {
        int secondSlash = path.indexOf('/', 1);

        String root;
        if(secondSlash == -1)
           root = path;
        else
            root = path.substring(0, secondSlash);
        return root;
    }

    public String getMethod()
    {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getParam(String key)
    {
        return params.getOrDefault(key, "");
    }

    public static class Builder extends HttpMessage.Builder<Builder, HttpMessage>
    {
        private final Map<String, String> params = new HashMap<>();
        private String path;
        private String method;

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

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Request build()
        {
            return new Request(headers, params, path, method, version, boundary, body);
        }

    }
}
