package lab.idioglossia.sloth.io;

import lab.idioglossia.sloth.model.ReadFileModel;
import lab.idioglossia.sloth.model.ReadFileOutput;
import lab.idioglossia.sloth.util.SharedLock;
import lab.idioglossia.sloth.util.TransactionManager;

import java.io.File;
import java.util.concurrent.Semaphore;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileReader {
    private final Semaphore semaphore;
    private final Pipeline<ReadFileModel, ReadFileOutput> readPipeline;

    public FileReader(int permits) {
        this.semaphore = new Semaphore(permits);
        this.readPipeline = PipelineFactory.getReadPipeline();
    }

    public byte[] readAsByteArray(File file){
        try {
            semaphore.acquire();
            ReadFileOutput readFileOutput = new ReadFileOutput();

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
                            readPipeline.run(new ReadFileModel(file), readFileOutput);
                        }
                    }).addTransaction(new TransactionManager.Transaction() {
                        @Override
                        public void process() {
                            SharedLock.getInstance().unlock(file.getPath().hashCode());
                        }
                    });

            transactionManager.commit();
            if(readFileOutput.getE() != null){
                throw readFileOutput.getE();
            }
            return readFileOutput.getBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            semaphore.release();
        }
    }

    public String readAsString(File file){
        byte[] bytes = readAsByteArray(file);
        return bytes != null ? new String(bytes, UTF_8) : null;
    }
}
