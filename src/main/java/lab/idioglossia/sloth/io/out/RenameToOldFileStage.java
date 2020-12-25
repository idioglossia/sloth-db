package lab.idioglossia.sloth.io.out;

import lab.idioglossia.sloth.io.Pipeline;
import lab.idioglossia.sloth.model.WriteFileModel;

import java.io.File;
import java.io.IOException;

public class RenameToOldFileStage implements Pipeline.Stage<WriteFileModel, Void> {

    @Override
    public boolean process(WriteFileModel writeFileModel, Void o) {
        File destFile = new File(writeFileModel.getFile().getAbsolutePath() + ".old");

        if(!destFile.exists()) {
            try {
                destFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        File file = writeFileModel.getFile();
        file.renameTo(destFile);
        return true;
    }

}
