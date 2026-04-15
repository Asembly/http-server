package asembly.httpserver.http.handler;

import asembly.httpserver.cache.Cache;
import asembly.httpserver.cache.LazyCache;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;
import asembly.httpserver.http.ResponseFabric;
import asembly.httpserver.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class StaticHandler implements Handler{
    private static final Logger log = LoggerFactory.getLogger(StaticHandler.class);

    private final FileService fileService;
    private final String filename;
    private final Cache<String, byte[]> cache;

    public StaticHandler(String filename){
        this.fileService = new FileService();
        this.filename = filename;
        cache = new LazyCache<>(fileService::getFile);
    }

    @Override
    public Response handle(Request request) {
        var cacheFile = cache.get(filename);
        var contentType = getContentType(Paths.get(filename));

        if(cacheFile.length == 0)
            return ResponseFabric.notFound();

        return ResponseFabric.ok(cacheFile, contentType);
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
