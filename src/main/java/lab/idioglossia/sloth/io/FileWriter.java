package lab.idioglossia.sloth.io;

import lab.idioglossia.sloth.model.WriteFileModel;
import lab.idioglossia.sloth.util.SharedLock;
import lab.idioglossia.sloth.util.TransactionManager;

import java.io.File;
import java.util.concurrent.Semaphore;

public class FileWriter {
    private final Pipeline<WriteFileModel, Void> pipeline;
    private final Semaphore semaphore;

    public FileWriter(int permits) {
        this.semaphore = new Semaphore(permits);
        pipeline = PipelineFactory.getWritePipeline();
    }

    public void write(File file, String content){
        try {
            semaphore.acquire();
            TransactionManager transactionManager = new TransactionManager(new TransactionManager.Config().setIgnoreRollbackExceptions(true))
                    .addTransaction(new TransactionManager.Transaction() {
                        @Override
                        public void process() {
                            try {
                                SharedLock.getInstance().lock(file.getPath().hashCode());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onRollBack() {
                            SharedLock.getInstance().unlock(file.getPath().hashCode());
                        }
                    }).addTransaction(new TransactionManager.Transaction() {
                        @Override
                        public void process() {
                            pipeline.run(new WriteFileModel(file, content), null);
                        }
                    }).addTransaction(new TransactionManager.Transaction() {
                        @Override
                        public void process() {
                            SharedLock.getInstance().unlock(file.getPath().hashCode());
                        }
                    });
            transactionManager.commit();
        } catch (Exception e) {
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
