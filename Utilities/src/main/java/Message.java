public class Message {
    private byte[] bytes;

    public Message(byte[] bytes) {
        this.bytes = bytes;
        clearBytesArray();
    }

    private void clearBytesArray() {
        byte[] clearMessage = new byte[this.bytes.length - 1];
        System.arraycopy(this.bytes, 1, clearMessage, 0, this.bytes.length -1);
        this.bytes = clearMessage;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public Message getMessageObject(){
        return this;
    }
}