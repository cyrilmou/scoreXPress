package fr.cm.scorexpress.ihm.view.node;

import fr.cm.scorexpress.core.model.AbstractSteps;
import fr.cm.scorexpress.core.model.impl.ObjStep;

import java.util.Collection;

public class NodeEpreuve {
    private final AbstractSteps steps;

    public NodeEpreuve(final AbstractSteps steps) {
        this.steps = steps;
    }

    public Collection<ObjStep> getSteps() {
        return steps.getSteps();
    }

    public String toString() {
        return "Epreuves";
    }
}
