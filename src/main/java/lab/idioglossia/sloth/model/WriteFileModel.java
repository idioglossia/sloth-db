package lab.idioglossia.sloth.model;

import java.io.File;

public class WriteFileModel {
    private final File file;
    private String content;
    private byte[] binaryContent;
    private boolean binary = false;

    public WriteFileModel(File file, byte[] binaryContent) {
        this.file = file;
        this.binaryContent = binaryContent;
        setBinary(true);
    }

    public WriteFileModel(File file, String content) {
        this.file = file;
        this.content = content;
    }

    public WriteFileModel(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getBinaryContent() {
        return binaryContent;
    }

    public void setBinaryContent(byte[] binaryContent) {
        this.binaryContent = binaryContent;
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }
}
