package fr.cm.scorexpress.ihm.view.clipboard;

import fr.cm.scorexpress.core.model.AbstractSteps;
import static fr.cm.scorexpress.ihm.view.dnd.StepTransfer.getStepTransfer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Event;

import java.util.Iterator;

public class CopyStepAction extends Action {
    private final Clipboard clipboard;
    private final StructuredViewer viewer;

    public CopyStepAction(final StructuredViewer viewer, final Clipboard clipboard) {
        super("Copy");
        this.viewer = viewer;
        this.clipboard = clipboard;
    }

    public void runWithEvent(final Event event) {
        final IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
        if (sel.getFirstElement() instanceof AbstractSteps) {
            final StringBuffer datas = new StringBuffer();
            final Iterator<AbstractSteps> iter = sel.iterator();
            while (iter.hasNext()) {
                datas.append(iter.next().getLib());
                if (iter.hasNext())
                    datas.append('\n');
            }
            clipboard.setContents(new Object[]{sel.getFirstElement(), datas.toString()},
                    new Transfer[]{getStepTransfer(), TextTransfer.getInstance()});
        }
    }
}
