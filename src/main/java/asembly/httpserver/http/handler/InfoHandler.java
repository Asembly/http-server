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

public class InfoHandler implements Handler{
    private static final Logger log = LoggerFactory.getLogger(InfoHandler.class);

    private final FileService fileService;
    private final JsonBodyParser jsonParser;
    private final MultipartBodyParser multipartParser;

    public InfoHandler(){
        this.multipartParser = new MultipartBodyParser();
        this.fileService = new FileService();
        this.jsonParser = new JsonBodyParser();
    }

    @Override
    public Response handle(Request request) {
        try{
            return ResponseFabric.ok(fileService.getFile("index.html"), "text/html");
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return ResponseFabric.notFound();
    }
}
