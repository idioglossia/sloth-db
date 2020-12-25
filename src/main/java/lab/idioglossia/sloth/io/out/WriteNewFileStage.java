package lab.idioglossia.sloth.io.out;

import lab.idioglossia.sloth.io.Pipeline;
import lab.idioglossia.sloth.model.WriteFileModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class WriteNewFileStage implements Pipeline.Stage<WriteFileModel, Void> {

    @Override
    public boolean process(WriteFileModel writeFileModel, Void o) {
        File file = writeFileModel.getFile();

        file = getNewFile(file);

        if(writeFileModel.isBinary())
            writeBinary(file, writeFileModel.getBinaryContent());
        else
            writeString(file, writeFileModel.getContent());
        return true;
    }

    private File getNewFile(File file) {
        File file1 = new File(file.getAbsolutePath() + ".new");
        if(file1.exists()){
            file1.delete();
        }
        return file1;
    }

    private void writeString(File file, String content){
        try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
            out.print(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeBinary(File file, byte[] bytes){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
