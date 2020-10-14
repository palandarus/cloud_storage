import java.io.Serializable;

public class FilePart implements Serializable {
    private String fileName;
private int partFileNumber;
private byte[] partFile;

    public FilePart(String fileName, int partFileNumber, byte[] partFile) {
        this.fileName=fileName;
        this.partFileNumber = partFileNumber;
        this.partFile = partFile;
    }

    public int getPartFileNumber() {
        return partFileNumber;
    }

    public byte[] getPartFile() {
        return partFile;
    }

    public void setPartFileNumber(int partFileNumber) {
        this.partFileNumber = partFileNumber;
    }

    public void setPartFile(byte[] partFile) {
        this.partFile = partFile;
    }

    public int getSize(){
        return partFile.length;
    }

    public String getFileName() {
        return fileName;
    }
}
