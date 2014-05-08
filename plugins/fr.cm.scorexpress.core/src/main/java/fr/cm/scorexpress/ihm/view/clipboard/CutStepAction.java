package fr.cm.scorexpress.ihm.view.clipboard;

import fr.cm.scorexpress.core.model.AbstractSteps;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import static fr.cm.scorexpress.ihm.view.dnd.StepTransfer.getStepTransfer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;

public class CutStepAction extends Action {
    private final Clipboard clipboard;
    private final StructuredViewer viewer;

    public CutStepAction(final StructuredViewer viewer, final Clipboard clipboard) {
        super("Cut");
        this.viewer = viewer;
        this.clipboard = clipboard;
    }

    public void run() {
        final IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
        final ObjStep step = (ObjStep) sel.getFirstElement();
        clipboard.setContents(new Object[]{step}, new Transfer[]{getStepTransfer()});
        final AbstractSteps steps = (AbstractSteps) step.getParent();
        steps.removeStep(step);
        viewer.refresh();
    }
}
