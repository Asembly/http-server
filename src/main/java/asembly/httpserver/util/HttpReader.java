package asembly.httpserver.util;

import java.io.IOException;
import java.io.InputStream;

public abstract class HttpReader {
    protected abstract <T extends HttpMessage> T read(InputStream input) throws IOException;

    protected String readLine(InputStream input) throws IOException {
        StringBuilder builder = new StringBuilder();
        int byteRead;
        while((byteRead = input.read()) != '\n')
        {
            if(byteRead == -1) break;
            if(byteRead != '\r') builder.append((char)byteRead);
        }

        if(builder.isEmpty() && byteRead == -1)
            throw new IOException("HTTP message sent empty");

        return builder.toString();
    }
}
