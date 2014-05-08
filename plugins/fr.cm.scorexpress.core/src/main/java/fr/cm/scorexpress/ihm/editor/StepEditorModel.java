package fr.cm.scorexpress.ihm.editor;

import fr.cm.scorexpress.model.StepModel;

public class StepEditorModel {
    private final StepModel stepModel;
    private final boolean dirty = false;

    public StepEditorModel(final StepModel stepModel) {
        this.stepModel = stepModel;
    }

    public String getNameEditor() {
        return stepModel.getStep().getLib();
    }

    public StepModel getStepModel() {
        return stepModel;
    }

    public void dispose() {
    }

    public void doSave() {
    }

    public boolean isDirty() {
        return dirty;
    }
}
