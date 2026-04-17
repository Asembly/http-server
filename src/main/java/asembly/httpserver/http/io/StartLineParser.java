package asembly.httpserver.http.io;

import asembly.httpserver.exception.HttpParseException;

import java.util.List;

@FunctionalInterface
public interface StartLineParser {

    List<String> parse(String line) throws HttpParseException;

}
