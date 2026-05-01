package asembly.httpserver.http.serialize;

import asembly.httpserver.enums.StatusCode;
import asembly.httpserver.http.Response;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ResponseSerializer {

    private ResponseSerializer() {}

    public static ByteBuffer toByteBuffer(Response response) {
        String startLine = String.join(" ",
                response.getVersion(),
                String.valueOf(response.getStatusCode()),
                StatusCode.stringFromCode(response.getStatusCode())
        ) + "\r\n";

        StringBuilder headersBuilder = new StringBuilder();
        for (var h : response.getHeaders().entrySet()) {
            headersBuilder.append(h.getKey())
                    .append(": ")
                    .append(h.getValue())
                    .append("\r\n");
        }

        String headPart = startLine + headersBuilder + "\r\n";
        byte[] headBytes = headPart.getBytes(StandardCharsets.UTF_8);
        byte[] body = response.getBody();

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
