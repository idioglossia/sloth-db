package lab.idioglossia.sloth.io;

import lab.idioglossia.sloth.model.WriteFileModel;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class FileWriter {
    private final Pipeline<WriteFileModel, Void> pipeline;
    private final Semaphore semaphore;
    private static final Set<String> lockedKeys = new HashSet<>();

    public FileWriter(int permits) {
        this.semaphore = new Semaphore(permits);
        pipeline = PipelineFactory.getWritePipeline();
    }

    public void write(File file, String content){
        try {
            semaphore.acquire();
            lock(file.getAbsolutePath());
            pipeline.run(new WriteFileModel(file, content), null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            unlock(file.getAbsolutePath());
            semaphore.release();
        }
    }

    public void write(File file, byte[] bytes){
        try {
            semaphore.acquire();
            lock(file.getAbsolutePath());
            pipeline.run(new WriteFileModel(file, bytes), null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            unlock(file.getAbsolutePath());
            semaphore.release();
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
