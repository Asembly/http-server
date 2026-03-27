package asembly.httpserver.handler;

import asembly.httpserver.model.Multipart;
import asembly.httpserver.parser.JsonBodyParser;
import asembly.httpserver.parser.MultipartBodyParser;
import asembly.httpserver.service.FileService;
import asembly.httpserver.util.Request;
import asembly.httpserver.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileHandler implements Handler{
    private static final Logger log = LoggerFactory.getLogger(FileHandler.class);

    private final FileService fileService;
    private final JsonBodyParser jsonParser;
    private final MultipartBodyParser multipartParser;

    public FileHandler(){
        this.multipartParser = new MultipartBodyParser();
        this.fileService = new FileService();
        this.jsonParser = new JsonBodyParser();
    }

    @Override
    public Response handle(Request request) {
        log.debug("File handler");

        var method = request.getMethod();

        try {
            switch (method) {
                case "POST": return post(request);
                case "GET": return get(request);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return new Response.Builder()
                .addHeader("Content-Type", "text/plain")
                .statusCode(400).build();
    }

    private Response get(Request request) throws IOException {
        var param = request.getParam("filename");
        var response = new Response.Builder();

        if(param.isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            sb.append("List of file on server\r\n\r\n");
            for(var file: fileService.getFiles())
            {
                sb.append(file.getFileName()).append("\r\n");
            }
            response.body(sb.toString().getBytes());
        }

        return response.build();
    }

    private Response post(Request request) throws IOException {
        var body = request.getBody();
        var contentType = request.getHeader("Content-Type");
        var response = new Response.Builder();
        response.statusCode(200)
                .addHeader("Content-Type", "text/plain")
                .body("Idi nahuy".getBytes());

        List<Multipart> multipart = new ArrayList();

        if(multipartParser.isParse(contentType))
            multipart.addAll(multipartParser.parse(body, request.getBoundary()));

        for(var part: multipart)
        {
            switch(part.headers.get("Content-Type"))
            {
                case "application/json":
                    break;
                case "image/png":
                    var filename = UUID.randomUUID().toString().substring(0, 8) + "image.png";
                    fileService.saveFile(filename, part.content);
                    break;
            }
        }
        return response.build();
    }
}
