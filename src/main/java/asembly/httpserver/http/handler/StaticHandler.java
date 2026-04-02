package asembly.httpserver.http.handler;

import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;
import asembly.httpserver.http.ResponseFabric;
import asembly.httpserver.parser.JsonBodyParser;
import asembly.httpserver.parser.MultipartBodyParser;
import asembly.httpserver.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class StaticHandler implements Handler{
    private static final Logger log = LoggerFactory.getLogger(StaticHandler.class);

    private final FileService fileService;
    private final JsonBodyParser jsonParser;
    private final MultipartBodyParser multipartParser;
    private final String filename;

    public StaticHandler(String filename){
        this.multipartParser = new MultipartBodyParser();
        this.fileService = new FileService();
        this.jsonParser = new JsonBodyParser();
        this.filename = filename;
    }

    @Override
    public Response handle(Request request) {
        try{
            return ResponseFabric.ok(fileService.getFile(filename), getContentType(Paths.get(filename)));
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return ResponseFabric.notFound();
    }

    private String getContentType(Path file) {
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);

        if (name.endsWith(".png"))  return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".gif"))  return "image/gif";
        if (name.endsWith(".svg"))  return "image/svg+xml";
        if (name.endsWith(".ico"))  return "image/x-icon";
        if (name.endsWith(".html"))  return "text/html";
        if (name.endsWith(".mp4"))  return "video/mp4";


        return "application/octet-stream";
    }
}
