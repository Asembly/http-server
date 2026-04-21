package asembly.httpserver.http.io;

import java.io.IOException;
import java.nio.channels.SelectionKey;

@FunctionalInterface
public interface HttpParser {
    void parse(SelectionKey key) throws IOException;
}
