package lab.idioglossia.sloth.util;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Predicate;

public class DBValuePathPredict implements Predicate<Path> {
    private final File file;

    public DBValuePathPredict(File file) {
        this.file = file;
    }

    @Override
    public boolean test(Path path) {
        return !PathUtil.cleanPath(path, file.getAbsolutePath()).startsWith(".");
    }
}