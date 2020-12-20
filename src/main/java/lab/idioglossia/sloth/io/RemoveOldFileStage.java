package lab.idioglossia.sloth.io;

import lab.idioglossia.sloth.model.WriteFileModel;

import java.io.File;
import java.io.IOException;

public class RemoveOldFileStage implements Pipeline.Stage<WriteFileModel, Void> {

    @Override
    public boolean process(WriteFileModel writeFileModel, Void o) {
        File old = new File(writeFileModel.getFile().getAbsolutePath() + ".old");

        if(old.exists()) {
            old.delete();
        }

        return true;
    }

}
