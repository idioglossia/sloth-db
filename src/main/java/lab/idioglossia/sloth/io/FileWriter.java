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
            pipeline.run(new WriteFileModel(file, content), null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            semaphore.release();
        }
    }

    public void write(File file, byte[] bytes){
        try {
            semaphore.acquire();
            pipeline.run(new WriteFileModel(file, bytes), null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            semaphore.release();
        }
    }
}
