package lab.idioglossia.sloth.io;

import lab.idioglossia.sloth.model.ReadFileModel;
import lab.idioglossia.sloth.model.ReadFileOutput;

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
            readPipeline.run(new ReadFileModel(file), readFileOutput);
            if(readFileOutput.getE() != null){
                throw new RuntimeException(readFileOutput.getE());
            }
            return readFileOutput.getBytes();
        } catch (InterruptedException e) {
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
