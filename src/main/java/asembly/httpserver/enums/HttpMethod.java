package asembly.httpserver.enums;

public enum HttpMethod {
    POST("POST"),
    GET("GET");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod methodFromString(String method)
    {
        for(HttpMethod sc: values())
        {
            if(sc.method.equals(method))
                return sc;
        }
        return null;
    }
}
