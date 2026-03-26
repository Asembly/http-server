package org.example.util;

import java.io.IOException;

public class MultipartBodyParser implements BodyParser{

    private final String boundary;

    public MultipartBodyParser(String boundary)
    {
       this.boundary = boundary;
    }

    @Override
    public boolean isParse(String body) {
        return false;
    }

    @Override
    public <T> T parse(byte[] body, Class<T> object) throws IOException {

        for (int i = 0; i < body.length; i++) {

        }

        return null;
    }
}
