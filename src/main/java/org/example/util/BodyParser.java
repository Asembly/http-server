package org.example.util;

import java.io.IOException;

public interface BodyParser {

    boolean isParse(String body);
    <T> T parse(byte[] body, Class<T> object) throws IOException;

}
