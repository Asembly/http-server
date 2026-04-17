package asembly.httpserver.http.io;

import asembly.httpserver.exception.HttpParseException;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface HttpParser {

    void parse(SelectionKey key) throws HttpParseException, IOException;
}
