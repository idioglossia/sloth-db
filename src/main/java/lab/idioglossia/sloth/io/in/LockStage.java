package lab.idioglossia.sloth.io.in;

import lab.idioglossia.sloth.io.Pipeline;
import lab.idioglossia.sloth.model.ReadFileModel;
import lab.idioglossia.sloth.model.ReadFileOutput;
import lab.idioglossia.sloth.util.SharedLock;

public class LockStage implements Pipeline.Stage<ReadFileModel, ReadFileOutput> {
    @Override
    public boolean process(ReadFileModel readFileModel, ReadFileOutput readFileOutput) {
        try {
            SharedLock.getInstance().lock(readFileModel.getFile().getAbsolutePath().hashCode());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
