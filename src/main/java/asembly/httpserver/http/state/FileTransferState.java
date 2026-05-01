package asembly.httpserver.http.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileTransferState {

    private static final Logger log = LoggerFactory.getLogger(FileTransferState.class);
    private final FileChannel fileChannel;

    private final long size;
    private long position;

    public FileTransferState(Path path) throws IOException {
        this.fileChannel = FileChannel.open(path, StandardOpenOption.READ);
        this.position = 0;
        this.size = fileChannel.size();
    }

    public long transferTo(SocketChannel socketChannel) throws IOException {
        long transferred = fileChannel.transferTo(position, size, socketChannel);
        if(transferred > 0)
            position += transferred;

        return transferred;
    }

    public boolean finished()
    {
        return position >= size;
    }

    public void close()
    {
        try{
            fileChannel.close();
        } catch (IOException e) {
            log.debug("{}\n{}",e.getMessage(), e.getStackTrace());
        }
    }

    public long getSize() {
        return size;
    }
}
