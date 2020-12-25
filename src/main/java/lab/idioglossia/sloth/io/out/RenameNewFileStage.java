package lab.idioglossia.sloth.io.out;

import lab.idioglossia.sloth.io.Pipeline;
import lab.idioglossia.sloth.model.WriteFileModel;

import java.io.File;
import java.io.IOException;

public class RenameNewFileStage implements Pipeline.Stage<WriteFileModel, Void> {

    @Override
    public boolean process(WriteFileModel writeFileModel, Void o) {
        File newFile = new File(writeFileModel.getFile().getAbsolutePath() + ".new");

        if(!newFile.exists()) {
            throw new RuntimeException(".new file doesn't exist");
        }

        File file = writeFileModel.getFile();
        return newFile.renameTo(file);
    }

}
