package lab.idioglossia.sloth.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Semaphore;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileReader {
    private final Semaphore semaphore;

    public FileReader(int permits) {
        semaphore = new Semaphore(permits);
    }

    public byte[] readAsByteArray(File file){
        try {
            semaphore.acquire();
            return Files.readAllBytes(file.toPath());
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }finally {
            semaphore.release();
        }
    }

    public String readAsString(File file){
        try {
            semaphore.acquire();
            byte[] encoded = Files.readAllBytes(file.toPath());
            return new String(encoded, UTF_8);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }finally {
            semaphore.release();
        }
    }
}
