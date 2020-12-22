package lab.idioglossia.sloth.io;

import lab.idioglossia.sloth.io.in.ReadFileStage;
import lab.idioglossia.sloth.io.out.*;
import lab.idioglossia.sloth.model.ReadFileModel;
import lab.idioglossia.sloth.model.ReadFileOutput;
import lab.idioglossia.sloth.model.WriteFileModel;

public class PipelineFactory {

    public static Pipeline<WriteFileModel, Void> getWritePipeline(){
        return new Pipeline<WriteFileModel, Void>()
                .addStage(new LockStage())
                .addStage(new WriteNewFileStage())
                .addStage(new RenameToOldFileStage())
                .addStage(new RenameNewFileStage())
                .addStage(new RemoveOldFileStage())
                .addStage(new UnLockStage());
    }

    public static Pipeline<ReadFileModel, ReadFileOutput> getReadPipeline(){
        return new Pipeline<ReadFileModel, ReadFileOutput>()
                .addStage(new lab.idioglossia.sloth.io.in.LockStage())
                .addStage(new ReadFileStage())
                .addStage(new lab.idioglossia.sloth.io.in.UnLockStage());
    }

}
