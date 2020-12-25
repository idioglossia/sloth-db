package lab.idioglossia.sloth.io;

import lab.idioglossia.sloth.io.in.ReadFileStage;
import lab.idioglossia.sloth.io.out.RemoveOldFileStage;
import lab.idioglossia.sloth.io.out.RenameNewFileStage;
import lab.idioglossia.sloth.io.out.RenameToOldFileStage;
import lab.idioglossia.sloth.io.out.WriteNewFileStage;
import lab.idioglossia.sloth.model.ReadFileModel;
import lab.idioglossia.sloth.model.ReadFileOutput;
import lab.idioglossia.sloth.model.WriteFileModel;

public class PipelineFactory {

    public static Pipeline<WriteFileModel, Void> getWritePipeline(){
        return new Pipeline<WriteFileModel, Void>()
                .addStage(new WriteNewFileStage())
                .addStage(new RenameToOldFileStage())
                .addStage(new RenameNewFileStage())
                .addStage(new RemoveOldFileStage());
    }

    public static Pipeline<ReadFileModel, ReadFileOutput> getReadPipeline(){
        return new Pipeline<ReadFileModel, ReadFileOutput>()
                .addStage(new ReadFileStage());
    }

}
