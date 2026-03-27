package org.example.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class JsonBodyParser implements BodyParser {

    private static final Logger log = LoggerFactory.getLogger(JsonBodyParser.class);
    private final ObjectMapper objectMapper;

    public JsonBodyParser() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean isParse(String body) {
        try {
            objectMapper.readTree(body);
            return true;
        } catch (JsonProcessingException e) {
            log.debug("The body is not json object");
            return false;
        }
    }

    public <T> T parse(byte[] body, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(Arrays.toString(body), clazz);
    }
}
