package asembly.httpserver.http.handler;

import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;
import asembly.httpserver.http.response.JsonResponseService;
import asembly.httpserver.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class AutoIndexHandler implements SyncHandler{

    private static final Logger log = LoggerFactory.getLogger(AutoIndexHandler.class);
    private final String root;
    private final String format;

    private final FileService fileService;

    public AutoIndexHandler(String root, String format)
    {
        this.fileService = new FileService();

        this.root = root;
        this.format = format;
    }

    @Override
    public Response handle(Request request) {

        var files = fileService.getFiles(root);

        log.info("List of files:\n{}",files);

        List<FileEntry> entries = files.stream()
                .map(p -> {
                    String name = p.getFileName().toString();
                    long size;
                    try {
                        size = Files.size(p);
                    } catch (IOException e) {
                        size = 0; // или -1, или вообще пробросить ошибку
                    }
                    return new FileEntry(name, size);
                })
                .toList();

        return JsonResponseService.ok(entries);
    }
}
