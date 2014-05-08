package fr.cm.scorexpress.model;

import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.model.impl.ObjStep;

import static fr.cm.scorexpress.core.model.impl.StepFactory.createStep;

public class StepModel {
    private final ObjStep          step;
    private final boolean          teamMode;
    private final AutoResizeColumn autoResizeColumn;

    public StepModel(final ObjStep step, final boolean teamMode, final AutoResizeColumn autoResizeColumn) {
        this.step = step;
        this.teamMode = teamMode;
        this.autoResizeColumn = autoResizeColumn;
    }

    public ObjStep getStep() {
        return step;
    }

    public String getName() {
        return step.getLib();
    }

    public boolean isTeamMode() {
        return teamMode;
    }

    public void addNewSubStep() {
        step.addStep(createStep("new"));
    }

    public AutoResizeColumn getAutoResizeColumn() {
        return autoResizeColumn;
    }
}
