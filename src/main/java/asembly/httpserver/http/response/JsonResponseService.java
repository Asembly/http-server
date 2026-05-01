package asembly.httpserver.http.response;

import asembly.httpserver.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

public class JsonResponseService {

    private final static ObjectMapper mapper = new ObjectMapper();

    public static Response internalError(String message) {
        HttpError dto = new HttpError(500, "INTERNAL_ERROR", message, null);
        return toJson(dto, 500);
    }

    public static Response notFound(String message, String path)
    {
        HttpError dto = new HttpError(404, "NOT_FOUND", message, path);
        return toJson(dto, 404);
    }

    public static Response forbidden(String message, String path)
    {
        HttpError dto = new HttpError(403, "FORBIDDEN", message, path);
        return toJson(dto, 403);
    }

    public static Response badGateway(String message, String path)
    {
        HttpError dto = new HttpError(502, "BAD_GATEWAY", message, path);
        return toJson(dto, 502);
    }

    public static Response badRequest(String message, String path)
    {
        HttpError dto = new HttpError(400, "BAD_REQUEST", message, path);
        return toJson(dto, 400);
    }

    public static Response ok(Object dto)
    {
        return toJson(dto, 200);
    }

    private static Response toJson(Object dto, int status) {
        try{
            String json = mapper.writeValueAsString(dto);
            byte[] body = json.getBytes(StandardCharsets.UTF_8);

            return ResponseFabric.of(body, status, "application/json; charset=utf-8");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
