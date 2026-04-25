package asembly.httpserver.service;

import asembly.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    private final Path rootDir = Paths.get(HttpServer.config.getStaticDir());

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
        Path file = Paths.get(rootDir + "/" + path).normalize();

        if (!Files.exists(file) || !Files.isRegularFile(file)) {
            return null;
        }

        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public long getSizeFile(String path) throws IOException {
        Path file = Paths.get(rootDir + "/" + path).normalize();
        return Files.size(file);
    }

    public long transferTo(SocketChannel channel) throws IOException {
        FileChannel fileChannel = FileChannel.open(Path.of("test.png"), StandardOpenOption.READ);
        long position = 0;
        long size = 100;

        long transferred = fileChannel.transferTo(position, size - position, channel);

        if(transferred > 0)
            position += transferred;

        return transferred;
    }

    public String strip(String path, int index, char symbol)
    {
       if(path.charAt(index) == symbol)
           path = path.substring(1);
       return path;
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