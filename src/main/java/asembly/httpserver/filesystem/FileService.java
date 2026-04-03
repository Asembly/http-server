package asembly.httpserver.filesystem;

import asembly.httpserver.HttpServer;
import asembly.httpserver.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    private final Path rootDir = Paths.get(HttpServer.getConfig().getStaticDir());

    public FileService()
    {
        try{
            if(Files.notExists(rootDir))
                Files.createDirectories(rootDir);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void saveFile(String filename, byte[] bytes) throws IOException {
        String filePath = String.join("/", rootDir.toString(), filename);
        try(FileOutputStream os = new FileOutputStream(filePath))
        {
            os.write(bytes);
            os.flush();
        }
    }

    public List<Path> getFiles()
    {
        List<Path> paths = new ArrayList<>();
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(rootDir))
        {
            for(Path path: stream)
            {
                if(Files.isRegularFile(path))
                {
                    paths.add(path);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return paths;
    }

    public byte[] getFile(String path){
        Path file = Paths.get(rootDir + path).normalize();

        if (!Files.exists(file) || !Files.isRegularFile(file)) {
            throw new ResourceNotFoundException(path);
        }

        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String getBaseFilename(String path)
    {
        StringBuilder baseFilename = new StringBuilder();
        int i = path.length()-1;
        while(i != path.lastIndexOf("/"))
        {
            baseFilename.append(path.charAt(i));
            i--;
        }
        return baseFilename.reverse().toString();
    }
}