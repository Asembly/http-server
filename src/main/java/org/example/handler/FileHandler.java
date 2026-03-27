package org.example.handler;

import org.example.service.FileService;
import org.example.parser.JsonBodyParser;
import org.example.parser.MultipartBodyParser;
import org.example.util.Request;
import org.example.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

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
    public void handle(Request request, OutputStream outputStream) {
        log.debug("File handler");

        var method = request.getMethod();

        try {
            switch (method) {
                case "POST":
                    handlePost(request, outputStream);
                case "GET":
                    handleGet(request, outputStream);
                    break;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void handleGet(Request request, OutputStream outputStream) throws IOException {
        var param = request.getParam("filename");
        var response = new Response.Builder(outputStream);

        if(param.isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            sb.append("List of file on server\r\n\r\n");
            for(var file: fileService.getFiles())
            {
                sb.append(file.getFileName()).append("\r\n");
            }
            response.body(sb.toString());
        }

        SocketHandler.send(response.build());
    }

    private void handlePost(Request request, OutputStream outputStream) throws IOException {
        var body = request.getBody();
        var response = new Response.Builder(outputStream);

        multipartParser.parse(body, request.getBoundary());

        log.debug("File created");

        response.contentType("application/json")
                .statusCode(200)
                .body("You loh");

        SocketHandler.send(response.build());
    }
}
