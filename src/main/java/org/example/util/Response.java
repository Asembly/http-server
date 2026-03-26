package org.example.util;

import java.util.HashMap;
import java.util.Map;

public class Response {

    private final Map<String, String> headers;

    private String body;

    private int statusCode;

    public Response(){
        this.headers = new HashMap<>();
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getBody()
    {
        return body;
    }

    public void addHeader(String key, String value)
    {
        headers.put(key, value);
    }

    public Map<String, String> getHeaders()
    {
       return headers;
    }
}
