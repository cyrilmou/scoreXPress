package fr.cm.scorexpress.ihm.view.clipboard;

import fr.cm.scorexpress.core.model.AbstractSteps;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import static fr.cm.scorexpress.ihm.view.dnd.StepTransfer.getStepTransfer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.Clipboard;

public class PasteTableGadgetAction extends Action {
    private final Clipboard clipboard;
    private final StructuredViewer viewer;

    public PasteTableGadgetAction(final StructuredViewer viewer, final Clipboard clipboard) {
        super("Paste");
        this.viewer = viewer;
        this.clipboard = clipboard;
    }

    public void run() {
        final ObjStep gadgets = (ObjStep) clipboard.getContents(getStepTransfer());
        if (gadgets == null)
            return;
        final IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
        ((AbstractSteps) sel.getFirstElement()).addStep(gadgets);
        viewer.refresh();
    }
}
