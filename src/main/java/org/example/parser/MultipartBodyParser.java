package org.example.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class MultipartBodyParser implements BodyParser {

    private static final Logger log = LoggerFactory.getLogger(MultipartBodyParser.class);

    @Override
    public boolean isParse(String body) {
        return false;
    }

    public <T> T parse(byte[] body, String boundary) {

        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.UTF_8);

        int i = 0, j = 0;
        while(i < body.length)
        {
            if(j == boundaryBytes.length-1)
            {
                log.debug("Find the end of boundary: {}", body[j]);
                break;
            }

            if(body[i] == boundaryBytes[j])
                j++;

            i++;
        }

        return null;
    }

}
