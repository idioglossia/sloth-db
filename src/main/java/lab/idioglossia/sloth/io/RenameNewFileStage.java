package lab.idioglossia.sloth.io;

import lab.idioglossia.sloth.model.WriteFileModel;

import java.io.File;
import java.io.IOException;

public class RenameNewFileStage implements Pipeline.Stage<WriteFileModel, Void> {

    @Override
    public boolean process(WriteFileModel writeFileModel, Void o) {
        File newFile = new File(writeFileModel.getFile().getAbsolutePath() + ".new");

        if(!newFile.exists()) {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        File file = writeFileModel.getFile();
        return new File(newFile.toURI()).renameTo(file);
    }

}
