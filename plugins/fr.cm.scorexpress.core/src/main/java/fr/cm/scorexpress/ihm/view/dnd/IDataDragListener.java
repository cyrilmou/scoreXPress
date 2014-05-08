package fr.cm.scorexpress.ihm.view.dnd;

import fr.cm.scorexpress.core.model.AbstractGetInfo;
import fr.cm.scorexpress.core.model.ObjPenalite;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import static fr.cm.scorexpress.ihm.view.dnd.PenaliteTransfer.toByteArray;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.PluginTransferData;

public class IDataDragListener extends DragSourceAdapter {
    private final Viewer viewer;

    public IDataDragListener(final Viewer viewer) {
        this.viewer = viewer;
    }

    public void dragFinished(final DragSourceEvent event) {
        if (!event.doit) {
            return;
        }
        if (event.detail == DND.DROP_MOVE) {
            final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
            final AbstractGetInfo<?> selected = (AbstractGetInfo<?>) selection.getFirstElement();
            if (selected != null) {
                if (selected instanceof ObjPenalite) {
                    final ObjStep step = (ObjStep) selected.getParent();
                    step.removePenalite((ObjPenalite) selected);
                    step.setParent(null);
                } else if (selected instanceof ObjStep) {
                    final ObjStep step = (ObjStep) selected.getParent();
                    step.removeStep((ObjStep) selected);
                    step.setParent(null);
                }
            }
            viewer.refresh();
        } else if (event.detail == DND.DROP_COPY) {
            viewer.refresh();
        }
    }

    public void dragSetData(final DragSourceEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
        if (StepTransfer.getStepTransfer().isSupportedType(event.dataType)) {
            event.data = selection.getFirstElement();
        } else if (PenaliteTransfer.getInstance().isSupportedType(event.dataType)) {
            event.data = selection.getFirstElement();
        } else if (PluginTransfer.getInstance().isSupportedType(event.dataType)) {
            if (selection.getFirstElement() instanceof ObjStep) {
                final ObjStep step = (ObjStep) selection.getFirstElement();
                final byte[] data = StepTransfer.getStepTransfer().toByteArray(step);
                event.data = new PluginTransferData("fr.cm.scorexpress.ihm.view.dnd.stepDrop", data);
            } else if (selection.getFirstElement() instanceof ObjPenalite) {
                final ObjPenalite penality = (ObjPenalite) selection.getFirstElement();
                final byte[] data = toByteArray(penality);
                event.data = new PluginTransferData("fr.cm.scorexpress.ihm.view.dnd.penaliteDrop", data);
            }
        }
    }

    public void dragStart(final DragSourceEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
        if (selection.isEmpty()) {
            event.doit = false;
        } else {
            final Object selected = selection.getFirstElement();
            event.doit = selected instanceof ObjPenalite || selected instanceof ObjStep;
        }
    }
}
