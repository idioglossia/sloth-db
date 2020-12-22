package lab.idioglossia.sloth.io.in;

import lab.idioglossia.sloth.io.Pipeline;
import lab.idioglossia.sloth.model.ReadFileModel;
import lab.idioglossia.sloth.model.ReadFileOutput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ReadFileStage implements Pipeline.Stage<ReadFileModel, ReadFileOutput> {
    @Override
    public boolean process(ReadFileModel readFileModel, ReadFileOutput readFileOutput) {
        try {
            File file = readFileModel.getFile();
            if(!file.exists()){
                tryReadFromOldFile(readFileModel, readFileOutput);
            }else {
                readFromFile(readFileModel, readFileOutput);
            }
        }catch (Exception e){
            readFileOutput.setE(e);
        }
        return true;
    }

    private void readFromFile(ReadFileModel readFileModel, ReadFileOutput readFileOutput) throws IOException {
        readFileOutput.setBytes(Files.readAllBytes(readFileModel.getFile().toPath()));
    }

    private void tryReadFromOldFile(ReadFileModel readFileModel, ReadFileOutput readFileOutput) throws IOException {
        File file = new File(readFileModel.getFile().getAbsolutePath() + ".old");
        if(file.exists()){
            readFileOutput.setBytes(Files.readAllBytes(file.toPath()));
            moveOld(file);
        }
    }

    private void moveOld(File file) throws IOException {
        File destFile = new File(file.getAbsolutePath().replace(".old", ""));

        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        boolean b = file.renameTo(destFile);
        if(!b){
            throw new IOException("Could not replace .old file with valid file");
        }
    }
}
