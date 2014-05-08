package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.scorexpress.model.StepModel;

public class ImportationStepPageModel {
    private final StepModel stepModel;

    public ImportationStepPageModel(final StepModel stepModel) {
        this.stepModel = stepModel;
    }

    public StepModel getStepModel() {
        return stepModel;
    }
}
