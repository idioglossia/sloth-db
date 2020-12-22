package lab.idioglossia.sloth.io.out;

import lab.idioglossia.sloth.io.Pipeline;
import lab.idioglossia.sloth.model.WriteFileModel;
import lab.idioglossia.sloth.util.SharedLock;

public class UnLockStage implements Pipeline.Stage<WriteFileModel, Void> {

    @Override
    public boolean process(WriteFileModel writeFileModel, Void o) {
        SharedLock.getInstance().unlock(writeFileModel.getFile().getAbsolutePath().hashCode());
        return true;
    }

}
