package lab.idioglossia.sloth.io;

import lab.idioglossia.sloth.model.WriteFileModel;

public class PipelineFactory {

    public static Pipeline<WriteFileModel, Void> getWritePipeline(){
        return new Pipeline<WriteFileModel, Void>()
                .addStage(new WriteNewFileStage())
                .addStage(new RenameToOldFileStage())
                .addStage(new RenameNewFileStage())
                .addStage(new RemoveOldFileStage());
    }

}
