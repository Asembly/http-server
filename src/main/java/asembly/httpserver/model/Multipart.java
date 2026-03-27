package asembly.httpserver.model;

import java.util.HashMap;
import java.util.Map;

public class Multipart {
    public Map<String, String> headers = new HashMap<>();
    public byte[] content;
}
