package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    private final Path rootDir = Paths.get("src/main/resources/files");

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

    public Optional<Path> getFile(String filename) throws IOException {
        try(Stream<Path> stream = Files.walk(rootDir))
        {
            Optional<Path> file = stream.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().equals(filename))
                    .findFirst();
            if(file.isPresent())
                return file;

            throw new FileNotFoundException();
        }
    }
}