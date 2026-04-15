package asembly.httpserver.http.io;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface HttpReader {

    byte[] readLine(ByteBuffer buffer) throws IOException;

}
