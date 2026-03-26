package org.example.util;

import java.io.IOException;

public class MultipartBodyParser implements BodyParser{

    @Override
    public boolean isParse(String body) {
        return false;
    }

    @Override
    public <T> T parse(String body, Class<T> object) throws IOException {
        return null;
    }
}
