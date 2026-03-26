package org.example.handler;

import org.example.service.FileService;
import org.example.util.JsonBodyParser;
import org.example.util.Request;
import org.example.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class FileHandler implements Handler{
    private static final Logger log = LoggerFactory.getLogger(FileHandler.class);

    private final FileService fileService;
    private final JsonBodyParser jsonParser;

    public FileHandler(){
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

        var filename = UUID.randomUUID().toString().substring(0,8) + request.getHeaders("X-Filename");

        fileService.saveFile(filename, body);

        log.debug("File created");

        response.contentType("application/json")
                .statusCode(200)
                .body("You loh");

        SocketHandler.send(response.build());
    }
}
