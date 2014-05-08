package fr.cm.scorexpress.ihm.view.dnd;

import fr.cm.scorexpress.core.model.AbstractSteps;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.PluginTransferData;

public class StepDragListener extends DragSourceAdapter {
    private final StructuredViewer viewer;

    public StepDragListener(final StructuredViewer viewer) {
        this.viewer = viewer;
    }

    public void dragFinished(final DragSourceEvent event) {
        if (!event.doit) {
            return;
        }
        // if the gadget was moved, remove it from the source viewer
        if (event.detail == DND.DROP_MOVE) {
            final IStructuredSelection selection = (IStructuredSelection) viewer
                    .getSelection();
            final ObjStep step = (ObjStep) selection.getFirstElement();
            if (step != null) {
                final AbstractSteps steps = (AbstractSteps) step.getParent();
                steps.removeStep(step);
                step.setParent(null);
            }
            viewer.refresh();
        } else if (event.detail == DND.DROP_COPY) {
            viewer.refresh();
        }
    }

    public void dragSetData(final DragSourceEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) viewer
                .getSelection();
        final ObjStep step = (ObjStep) selection.getFirstElement();
        if (StepTransfer.getStepTransfer().isSupportedType(event.dataType)) {
            event.data = step;
        } else if (PluginTransfer.getInstance().isSupportedType(event.dataType)) {
            final byte[] data = StepTransfer.getStepTransfer().toByteArray(step);
            event.data = new PluginTransferData(
                    "fr.cm.scorexpress.ihm.view.dnd.stepDrop", data);
        }
    }

    public void dragStart(final DragSourceEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) viewer
                .getSelection();
        if (!selection.isEmpty()
                && selection.getFirstElement() instanceof ObjStep) {
            event.doit = true;
        } else {
            event.doit = false;
        }
    }
}
