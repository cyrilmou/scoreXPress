package fr.cm.scorexpress.ihm.view.dnd;

import fr.cm.scorexpress.core.model.AbstractSteps;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

public class StepTreeDropAdapter extends ViewerDropAdapter {
    public StepTreeDropAdapter(final Viewer viewer) {
        super(viewer);
    }

    public boolean performDrop(final Object data) {
        AbstractSteps target = (AbstractSteps) getCurrentTarget();
        if (target == null)
            target = (AbstractSteps) getViewer().getInput();
        final ObjStep toDrop = (ObjStep) data;
        final AbstractTreeViewer viewer = (AbstractTreeViewer) getViewer();
        if (toDrop.equals(target) || toDrop.getLib().equals(target.getLib()))
            return false;

        target.addStep(toDrop);
        viewer.add(target, toDrop);
        viewer.reveal(toDrop);
        return true;
    }

    public boolean validateDrop(final Object target, final int op, final TransferData type) {
        return target instanceof AbstractSteps && StepTransfer.getStepTransfer().isSupportedType(type);
    }
}
