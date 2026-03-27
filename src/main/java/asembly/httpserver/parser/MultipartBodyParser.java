package asembly.httpserver.parser;

import asembly.httpserver.model.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultipartBodyParser {

    private static final Logger log = LoggerFactory.getLogger(MultipartBodyParser.class);

    public boolean isParse(String contentType) {
        return "multipart/form-data".equals(contentType);
    }

    public List<Multipart> parse(byte[] body, String boundary) throws IOException {
        List<Multipart> parts = new ArrayList<>();

        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.US_ASCII);
        byte[] doubleCrlf = "\r\n\r\n".getBytes(StandardCharsets.US_ASCII);

        int pos = 0;

        pos = indexOf(body, boundaryBytes, pos);
        if (pos == -1) return parts;
        pos += boundaryBytes.length;

        if (body[pos] == '\r' && body[pos + 1] == '\n') {
            pos += 2;
        }

        while (pos < body.length) {

            if (startsWith(body, pos - boundaryBytes.length, boundaryBytes)
                    && body[pos] == '-' && body[pos + 1] == '-') {
                break;
            }

            int headersEnd = indexOf(body, doubleCrlf, pos);
            if (headersEnd == -1) break;

            String headersText = new String(body, pos, headersEnd - pos, StandardCharsets.US_ASCII);
            Multipart part = new Multipart();
            try (BufferedReader br = new BufferedReader(new StringReader(headersText))) {
                String line;
                while ((line = br.readLine()) != null && !line.isEmpty()) {
                    int idx = line.indexOf(':');
                    if (idx > 0) {
                        String key = line.substring(0, idx).trim();
                        String value = line.substring(idx + 1).trim();
                        part.headers.put(key, value);
                    }
                }
            }

            int contentStart = headersEnd + doubleCrlf.length;

            int nextBoundaryPos = indexOf(body, boundaryBytes, contentStart);
            if (nextBoundaryPos == -1) break;

            int contentEnd = nextBoundaryPos - 2;
            part.content = Arrays.copyOfRange(body, contentStart, contentEnd);

            parts.add(part);

            pos = nextBoundaryPos + boundaryBytes.length;
            if (pos + 1 < body.length && body[pos] == '-' && body[pos + 1] == '-') {
                break;
            }
            if (body[pos] == '\r' && body[pos + 1] == '\n') {
                pos += 2;
            }
        }

        return parts;
    }

    private int indexOf(byte[] data, byte[] pattern, int from) {
        outer:
        for (int i = from; i <= data.length - pattern.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private boolean startsWith(byte[] data, int from, byte[] pattern) {
        if (from < 0 || from + pattern.length > data.length) return false;
        for (int i = 0; i < pattern.length; i++) {
            if (data[from + i] != pattern[i]) return false;
        }
        return true;
    }
}
