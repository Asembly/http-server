package asembly.httpserver.http.handler;

import asembly.httpserver.cache.Cache;
import asembly.httpserver.cache.FileCache;
import asembly.httpserver.exception.ResourceMaxSizeException;
import asembly.httpserver.exception.ResourceNotFoundException;
import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;
import asembly.httpserver.http.response.JsonResponseService;
import asembly.httpserver.http.response.ResponseFabric;
import asembly.httpserver.http.response.ResponseSerializer;
import asembly.httpserver.service.FileService;
import asembly.httpserver.state.ClientState;
import asembly.httpserver.state.FileTransferState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class StaticHandler implements AsyncHandler {
    private static final Logger log = LoggerFactory.getLogger(StaticHandler.class);

    private final FileService fileService;
    private final Cache<String, byte[]> cache;

    public StaticHandler(){
        this.fileService = new FileService();
        cache = new FileCache(fileService);
    }

    @Override
    public void handle(Request request, SelectionKey key) throws IOException {
        ClientState state = (ClientState) key.attachment();

        String path = fileService.strip(request.getPath(), 0, '/');
        var lastSlash = path.indexOf("/", 1);
        var filename = path.substring(lastSlash+1);

        var contentType = getContentType(Paths.get(filename));

        try{
            //Cache found
            var cacheFile = cache.get(filename);
            var response = ResponseFabric.of(cacheFile, 200, contentType);
            state.setOutput(ResponseSerializer.toByteBuffer(response));

        } catch (ResourceNotFoundException e) {
            //File not found
            var response = JsonResponseService.notFound("Resource not found", request.getPath());
            state.setOutput(ResponseSerializer.toByteBuffer(response));

        } catch (ResourceMaxSizeException e) {
            //File have a max size
            FileTransferState fileState = new FileTransferState(Paths.get(request.getPath().substring(1)));
            state.setFileState(fileState);

            var response = new Response.Builder()
                    .statusCode(200)
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Content-Type", contentType)
                    .addHeader("Content-Length", String.valueOf(fileState.getSize()))
                    .addHeader("Accept-Ranges", String.valueOf(fileState.getSize()))
                    .version("HTTP/1.1")
                    .build();

            state.setOutput(ResponseSerializer.toByteBuffer(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            key.interestOps(SelectionKey.OP_WRITE);
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
        if (name.endsWith(".wav"))  return "audio/wav";

        return "application/octet-stream";
    }
}
