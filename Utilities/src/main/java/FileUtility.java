import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtility {

    public static String FILENAME_POSTFIX=".partFile";
    public static final String FILENAME_DELIMETER="⊗";
    public static final String FILEDIR_DELIMETER="\\";

    /**
     * Метод разбивает файл на массивы байтов, переданным размером,
     * и формирует лист объектов FilePart в которых хранятся массив
     * байтов, имя и порядкой номер для последующей сборки
     * */

    public static void splitAndSend(File f, int bufferSize, SocketChannel channel) throws IOException {
        int partCounter = 1;

        byte[] buffer = new byte[bufferSize];

        String fileName = f.getName();

        try (FileInputStream fis = new FileInputStream(f);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            int bytesAmount = 0;
            while ((bytesAmount = bis.read(buffer)) > 0) {
                byte[] arr = new byte[bytesAmount];
                System.arraycopy(buffer, 0, arr, 0, bytesAmount);
                channel.writeAndFlush(new FilePart(fileName, partCounter, arr));
                partCounter++;
            }
        }
    }


    public static void splitAndSend(File f, int bufferSize, Channel channel) throws IOException {
        int partCounter = 1;

        byte[] buffer = new byte[bufferSize];

        String fileName = f.getName();

        try (FileInputStream fis = new FileInputStream(f);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            int bytesAmount = 0;
            while ((bytesAmount = bis.read(buffer)) > 0) {
                byte[] arr = new byte[bytesAmount];
                System.arraycopy(buffer, 0, arr, 0, bytesAmount);
                channel.writeAndFlush(new FilePart(fileName, partCounter, arr));
                partCounter++;
            }
        }
    }



    public static File mergeFiles(List<Path> paths, File into)
            throws IOException {
        try (FileOutputStream fos = new FileOutputStream(into);
             BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
            for (Path p : paths) {
                Files.copy(p, mergingStream);
                Files.delete(p);
            }
        }
        return into;
    }

    public static List<FileInfo> getFileList(String folderPath) throws IOException {

        Path path = Paths.get(folderPath);
        return Files.list(path).map(FileInfo::new).collect(Collectors.toList());

    }

    public static void createUserFolder(String userDirectory) throws IOException {
        if(!isDirExist(userDirectory)) Files.createDirectory(Paths.get(userDirectory));
    }

    public static boolean isDirExist(String path){
        return Files.exists(Paths.get(path));
    }
}
