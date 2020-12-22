package lab.idioglossia.sloth.model;

public class ReadFileOutput {
    private Exception e;
    private byte[] bytes;

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
