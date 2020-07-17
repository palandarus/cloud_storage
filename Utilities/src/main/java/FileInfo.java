import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo implements Serializable {
    private String fileName;
    private long fileLength;
    private String filePath;
    private String fullFilePath;

    public FileInfo(Path path) {
        try {
            this.fileName = path.getFileName().toString();
            this.filePath=path.getParent().toString()+"\\";
            if (Files.isDirectory(path)) {
                this.fileLength = -1L;
            } else {
                this.fileLength = Files.size(path);
                this.fullFilePath=path.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException("Something wrong with file : " + path.getFileName().toString());
        }
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileInfo(String filename, Long length, String path) {
        this.fileName = filename;
        this.fileLength = length;
        this.filePath = path;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isDirectory() {
        return fileLength == -1L;
    }

    public boolean isUpDirectory() {
        return fileLength == -2L;
    }


    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileLength() {
        return fileLength;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFullFilePath(){
        return fullFilePath;
    }

    @Override
    public String toString() {
        return String.format(fileName);
    }
}
