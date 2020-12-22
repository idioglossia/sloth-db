package lab.idioglossia.sloth.model;

import java.io.File;

public class ReadFileModel {
    private final File file;

    public ReadFileModel(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
