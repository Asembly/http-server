package asembly.httpserver.http.handler;

import asembly.httpserver.cache.Cache;
import asembly.httpserver.cache.FileCache;
import asembly.httpserver.exception.ResourceNotFoundException;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;
import asembly.httpserver.http.response.JsonResponseService;
import asembly.httpserver.http.response.ResponseFabric;
import asembly.httpserver.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class StaticHandler implements SyncHandler {
    private static final Logger log = LoggerFactory.getLogger(StaticHandler.class);

    private final FileService fileService;
    private final Cache<String, byte[]> cache;

    public StaticHandler(){
        this.fileService = new FileService();
        cache = new FileCache(fileService);
    }

    @Override
    public Response handle(Request request) {
        var path = request.getPath();
        var lastSlash = path.indexOf("/", 1);
        var filename = path.substring(lastSlash+1);

        try{
            var cacheFile = cache.get(filename);
            var contentType = getContentType(Paths.get(filename));

            return ResponseFabric.of(cacheFile, 200, contentType);
        } catch (ResourceNotFoundException e) {
            return JsonResponseService.notFound("Resource not found", request.getPath());
        }


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
        if (name.endsWith(".mp3"))  return "audio/mpeg";

        return "application/octet-stream";
    }
}
