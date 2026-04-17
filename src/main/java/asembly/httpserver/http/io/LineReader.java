package asembly.httpserver.http.io;

import java.nio.ByteBuffer;

public class RequestReader implements HttpReader{

    private final byte[] CRLF = "\r\n".getBytes();

    @Override
    public byte[] readLine(ByteBuffer buffer) {

        int n = indexOf(buffer, CRLF);
        if (n == -1) {
            return null;
        }

        int startPos = buffer.position();
        int lineLength = n - startPos;

        if (lineLength < 0) {
            throw new IllegalStateException("n < position: n=" + n + ", position=" + startPos);
        }

        byte[] lineBytes = new byte[lineLength];
        buffer.get(lineBytes);

        buffer.position(n + CRLF.length);

        return lineBytes;
    }


    private int indexOf(ByteBuffer buffer, byte[] pattern) {
        int limit = buffer.limit();
        for (int i = buffer.position(); i <= limit - pattern.length; i++) {
            boolean match = true;
            for (int j = 0; j < pattern.length; j++) {
                if (buffer.get(i + j) != pattern[j]) {
                    match = false;
                    break;
                }
            }
            if (match)
                return i;
        }
        return -1;
    }
}
