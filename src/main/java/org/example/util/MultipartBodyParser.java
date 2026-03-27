package org.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class MultipartBodyParser implements BodyParser{

    private static final Logger log = LoggerFactory.getLogger(MultipartBodyParser.class);

    @Override
    public boolean isParse(String body) {
        return false;
    }

    public <T> T parse(byte[] body, String boundary) {

        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < body.length; i++) {
            for (int j = i; j < body.length; j++) {
                if(body[i] == body[j])
                {
                    log.debug("i : {}, j : {}",body[i], body[j]);
                }
            }
        }

        return null;
    }
}
