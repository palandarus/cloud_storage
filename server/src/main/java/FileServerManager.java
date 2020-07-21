import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public class FileServerManager {




    public static List<FileInfo> getFileList() throws IOException {

        Path path = Paths.get(Directories.SERVER_FILES_DIRECTORY.getPath());
        return Files.list(path).map(FileInfo::new).collect(Collectors.toList());

    }

    public static List<FileInfo> getFileList(Path path) throws IOException {

        return Files.list(path).map(FileInfo::new).collect(Collectors.toList());

    }



}
