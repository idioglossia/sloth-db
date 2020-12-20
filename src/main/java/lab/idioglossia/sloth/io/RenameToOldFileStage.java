package lab.idioglossia.sloth.io;

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
        return new File(file.toURI()).renameTo(destFile);
    }

}
