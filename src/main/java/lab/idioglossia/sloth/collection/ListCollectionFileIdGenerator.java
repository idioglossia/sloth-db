package lab.idioglossia.sloth.collection;

import lab.idioglossia.sloth.util.DBValuePathPredict;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListCollectionFileIdGenerator {
    private volatile Integer current = 1;

    public ListCollectionFileIdGenerator(File file, String extension) {
        try {
            Stream<Path> stream = Files.list(file.toPath());
            List<Path> paths = stream.filter(new DBValuePathPredict(file)).sorted(new Comparator<Path>() {
                @Override
                public int compare(Path o1, Path o2) {
                    return parseInteger(o1, extension, file).compareTo(parseInteger(o2, extension, file));
                }
            }).collect(Collectors.toList());

            if(paths.size() > 0){
                Path path = paths.get(paths.size() - 1);
                current = parseInteger(path, extension, file) + 1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void increase() {
        current++;
    }

    public synchronized Integer getId() {
        return current;
    }

    public synchronized void decrease() {
        current--;
    }

    private Integer parseInteger(Path path, String extension, File file){
        return Integer.parseInt(path.toString()
                .replaceAll(extension, "")
                .replaceAll(file.getAbsolutePath(), "")
                .replaceAll("/", "")
                .replaceAll("\\\\", "")
        );
    }
}
