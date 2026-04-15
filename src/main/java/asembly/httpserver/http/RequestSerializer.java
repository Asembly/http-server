package asembly.httpserver.http;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RequestSerializer {

    private RequestSerializer() {}

    public static ByteBuffer toByteBuffer(Request request) {
        String startLine = String.join(" ",
                request.getMethod(),
                request.getPath(),
                request.getVersion()) + "\r\n";

        StringBuilder headersBuilder = new StringBuilder();
        for (var h : request.getHeaders().entrySet()) {
            headersBuilder.append(h.getKey())
                    .append(": ")
                    .append(h.getValue())
                    .append("\r\n");
        }

        String headPart = startLine + headersBuilder + "\r\n";
        byte[] headBytes = headPart.getBytes(StandardCharsets.UTF_8);
        byte[] body = request.getBody();

        int bodyLen = body == null ? 0 : body.length;
        ByteBuffer buf = ByteBuffer.allocate(headBytes.length + bodyLen);
        buf.put(headBytes);
        if (bodyLen > 0) {
            buf.put(body);
        }
        buf.flip();
        return buf;
    }

}
