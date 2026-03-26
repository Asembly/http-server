package org.example.handler;

import org.example.model.CreateFileDto;
import org.example.service.FileService;
import org.example.util.JsonBodyParser;
import org.example.util.Request;
import org.example.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;

public class FileHandler implements Handler{
    private static final Logger log = LoggerFactory.getLogger(FileHandler.class);

    private final FileService fileService;
    private final JsonBodyParser jsonParser;

    public FileHandler(){
        this.fileService = new FileService();
        this.jsonParser = new JsonBodyParser();
    }

    @Override
    public void handle(Request request, Response response) {
        log.debug("File handler");

        var method = request.getMethod();

        try {
            switch (method) {
                case "POST":
                    handlePost(request, response);
                case "GET":
                    handleGet(request, response);
                    break;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void handleGet(Request request, Response response) throws IOException {
        var param = request.getParam("filename");

        if(param.isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            sb.append("List of file on server\r\n\r\n");
            for(var file: fileService.getFiles())
            {
                sb.append(file.getFileName()).append("\r\n");
            }
            response.setBody(sb.toString());
        }
        else
        {
            var file = fileService.getFile(param);
            file.ifPresent(path -> response.setBody(path.toString()));
        }
    }

    private void handlePost(Request request, Response response) throws IOException {
        var body = request.getBody();

        if (jsonParser.isParse(body)) {
            var dto = jsonParser.parse(body, CreateFileDto.class);
            log.debug("Body can be parsed: {}", dto);
            var resBody = "you loh";
            var contentLength = resBody.getBytes().length;
            var decoder = Base64.getDecoder();
            fileService.saveFile(dto.filename(), decoder.decode(dto.base64().getBytes()));
            log.debug("File created");
            response.addHeader("Content-Type", "text/plain");
            response.addHeader("Content-Length", String.valueOf(contentLength));
            response.addHeader("Connection", "close");
            response.setStatusCode(200);
            response.setBody(resBody);
        }
    }
}
