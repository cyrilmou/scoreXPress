package fr.cm.scorexpress.ihm.action;

import fr.cm.scorexpress.ihm.editor.IAutoAjustColumnEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public class AutoAdjustColumnAction implements IEditorActionDelegate {
    private IAutoAjustColumnEditor editor = null;

    @Override
    public void setActiveEditor(final IAction action, final IEditorPart targetEditor) {
        if (targetEditor instanceof IAutoAjustColumnEditor) {
            action.setEnabled(true);
            editor = (IAutoAjustColumnEditor) targetEditor;
            action.setChecked(editor.getAutoResizeContext().isAutoResize());
        } else {
            action.setEnabled(false);
        }
    }

    @Override
    public void run(final IAction action) {
        editor.getAutoResizeContext().setAutoResize(action.isChecked());
    }

    @Override
    public void selectionChanged(final IAction action, final ISelection selection) {
    }

}
