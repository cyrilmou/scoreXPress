package fr.cm.scorexpress.ihm.view.dnd;

import fr.cm.scorexpress.core.model.ObjPenalite;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import static fr.cm.scorexpress.ihm.view.dnd.StepTransfer.getStepTransfer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

public class PenaliteTreeDropAdapter extends ViewerDropAdapter {
    public PenaliteTreeDropAdapter(final Viewer viewer) {
        super(viewer);
    }

    public boolean performDrop(final Object data) {
        ObjStep target = (ObjStep) getCurrentTarget();
        if (target == null)
            target = (ObjStep) getViewer().getInput();
        final ObjPenalite toDrop = (ObjPenalite) data;
        final AbstractTreeViewer viewer = (AbstractTreeViewer) getViewer();
        if (target == null)
            return false;

        target.addPenalite(toDrop);
        viewer.add(target, toDrop);
        viewer.reveal(toDrop);
        return true;
    }

    public boolean validateDrop(final Object target, final int op, final TransferData type) {
        if (target instanceof ObjStep) {
            return getStepTransfer().isSupportedType(type);
        } else
            return false;
    }
}
