package lab.idioglossia.sloth.io;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class FileWriter {
    private final Semaphore semaphore;
    private static final Set<String> lockedKeys = new HashSet<>();

    public FileWriter(int permits) {
        this.semaphore = new Semaphore(permits);
    }

    public void write(File file, String content){
        PrintStream out = null;
        try {
            semaphore.acquire();
            lock(file.getAbsolutePath());
            out = new PrintStream(new FileOutputStream(file));
            out.print(content);
        } catch (InterruptedException | FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            unlock(file.getAbsolutePath());
            semaphore.release();
            if (out != null) {
                out.close();
            }
        }
    }

    public void write(File file, byte[] bytes){
        FileOutputStream fos = null;
        try {
            semaphore.acquire();
            lock(file.getAbsolutePath());
            fos = new FileOutputStream(file);
            fos.write(bytes);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            unlock(file.getAbsolutePath());
            semaphore.release();
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void lock(String key) throws InterruptedException {
        synchronized (lockedKeys) {
            while (!lockedKeys.add(key)) {
                lockedKeys.wait();
            }
        }
    }

    private void unlock(String key) {
        synchronized (lockedKeys) {
            lockedKeys.remove(key);
            lockedKeys.notifyAll();
        }
    }
}
