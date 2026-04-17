package asembly.httpserver.http.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public interface HttpParser {

    void parse(SelectionKey key);

}
