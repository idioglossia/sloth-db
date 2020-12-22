package lab.idioglossia.sloth.io.out;

import lab.idioglossia.sloth.io.Pipeline;
import lab.idioglossia.sloth.model.WriteFileModel;
import lab.idioglossia.sloth.util.SharedLock;

public class LockStage implements Pipeline.Stage<WriteFileModel, Void> {

    @Override
    public boolean process(WriteFileModel writeFileModel, Void o) {
        try {
            SharedLock.getInstance().lock(writeFileModel.getFile().getAbsolutePath().hashCode());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}
