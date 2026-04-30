package asembly.httpserver.cache;

import asembly.httpserver.HttpServer;
import asembly.httpserver.exception.ResourceMaxSizeException;
import asembly.httpserver.exception.ResourceNotFoundException;
import asembly.httpserver.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FileCache implements Cache<String, byte[]>{

    private static final Logger log = LoggerFactory.getLogger(FileCache.class);
    private final ConcurrentMap<String, byte[]> cache = new ConcurrentHashMap<>();
    private final FileService fileService;

    private static final long MAX_FILE_SIZE = HttpServer.config.cacheConfig.maxEntryBytes(); //Mb
    private static final long MAX_TOTAL_CACHE = HttpServer.config.cacheConfig.maxBytes(); //Mb

    private static int totalCacheSize = 0;

    public FileCache(FileService fileService)
    {
       this.fileService = fileService;
    }

    public FileCache()
    {
        this.fileService = new FileService();
    }

    @Override
    public byte[] get(String key) throws IOException {
        var value = cache.get(key);

        if(value != null) {
            return value;
        }

        try{
            var fileSize = fileService.getSizeFile(key);
            if(fileSize > MAX_FILE_SIZE)
            {
                log.debug("File is more bigger than max file size!");
                throw new ResourceMaxSizeException();
            }
            else if(totalCacheSize > MAX_TOTAL_CACHE)
            {
                log.debug("Cache is full loaded!");
                return fileService.getFile(key);
            }

            value = fileService.getFile(key);

            if(value == null)
                throw new ResourceNotFoundException();

            if(HttpServer.config.cacheConfig.enabled())
            {
                cache.put(key, value);
                totalCacheSize += fileSize;
                log.debug("File put to cache with size: {} kB", fileSize / 1024);
            }
        } catch (NoSuchFileException e) {
            throw new ResourceNotFoundException();
        }

        return value;
    }

    @Override
    public void put(String key, byte[] value) {
        cache.put(key, value);
    }
}
