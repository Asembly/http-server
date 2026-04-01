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

        return get(request);
    }

    private Response get(Request request) {
        try{
            var response = ResponseFabric.ok(fileService.getFile("index.html"), "text/html");
            return response;
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return ResponseFabric.notFound();
    }

    // TODO на переделку, сделать нормальную работу с файлом, парсить filename из заголовков
//    private Response post(Request request) throws IOException {
//        var body = request.getBody();
//        var contentType = request.getHeader("Content-Type");
//
//        List<Multipart> multipart = new ArrayList();
//
//        if(multipartParser.isParse(contentType))
//            multipart.addAll(multipartParser.parse(body, request.getBoundary()));
//
//        for(var part: multipart)
//        {
//            switch(part.headers.get("Content-Type"))
//            {
//                case "application/json":
//                    break;
//                case "image/png":
//                    var filename = UUID.randomUUID().toString().substring(0, 8) + "image.png";
//                    fileService.saveFile(filename, part.content);
//                    break;
//            }
//        }
//        return ResponseFabric.notFound();
//    }
}
