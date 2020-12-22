package lab.idioglossia.sloth.io.out;

import lab.idioglossia.sloth.io.Pipeline;
import lab.idioglossia.sloth.model.WriteFileModel;

import java.io.File;

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
