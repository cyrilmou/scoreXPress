package fr.cm.scorexpress.ihm.view.clipboard;

import fr.cm.scorexpress.core.model.AbstractSteps;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import static fr.cm.scorexpress.ihm.view.dnd.StepTransfer.getStepTransfer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.Clipboard;

public class PasteTreeGadgetAction extends Action {
    private final Clipboard clipboard;
    private final StructuredViewer viewer;

    public PasteTreeGadgetAction(final StructuredViewer viewer, final Clipboard clipboard) {
        super("Paste");
        this.viewer = viewer;
        this.clipboard = clipboard;
    }

    public void run() {
        final IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
        AbstractSteps parent = (AbstractSteps) sel.getFirstElement();
        if (parent == null)
            parent = (AbstractSteps) viewer.getInput();
        final ObjStep step = (ObjStep) clipboard.getContents(getStepTransfer());
        if (step == null)
            return;
        if (step.equals(parent))
            return;
        parent.addStep(step);
        viewer.refresh();
    }
}
