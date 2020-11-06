package lab.idioglossia.sloth;

import java.nio.file.Path;

public class PathUtil {
    public static String cleanPath(Path input, String clean){
        return input.toString().replaceAll(clean, "").replaceAll("/", "").replaceAll("\\\\", "");
    }
}
