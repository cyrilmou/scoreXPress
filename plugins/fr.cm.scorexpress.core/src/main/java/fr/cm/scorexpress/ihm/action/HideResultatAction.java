package fr.cm.scorexpress.ihm.action;

import fr.cm.scorexpress.ihm.editor.IAutoAjustColumnEditor;
import fr.cm.scorexpress.ihm.editor.ResultatEtapeEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public class HideResultatAction implements IEditorActionDelegate {
    public static final String      ID_HIDE_DECLASSE = "fr.cm.chronos.actions.HideDeclasse";
    public static final String      ID_HIDE_ABANDON  = "fr.cm.chronos.actions.HideAbandon";
    public static final String      ID_SIGNAL_ERROR  = "fr.cm.chronos.actions.SignalError";
    public static final String      ID_AJUSTE_COLUMN = "fr.cm.chronos.actions.AdjustColumn";
    private             IEditorPart editor           = null;

    public void setActiveEditor(final IAction action, final IEditorPart targetEditor) {
        editor = targetEditor;
        updateCheckOption(action, editor);
    }

    private void updateCheckOption(final IAction action, final IEditorPart editor) {
        this.editor = editor;
        if (editor == null) {
            return;
        }
        if (editor instanceof ResultatEtapeEditor) {
            final ResultatEtapeEditor resultEditor = (ResultatEtapeEditor) editor;
            if (action.getId().equals(ID_HIDE_DECLASSE)) {
                action.setChecked(resultEditor.getModel().isHideDeclasse());
            }
            if (action.getId().equals(ID_HIDE_ABANDON)) {
                action.setChecked(resultEditor.getModel().isHideAbandon());
            }
            if (action.getId().equals(ID_SIGNAL_ERROR)) {
                action.setChecked(resultEditor.getModel().isSignalError());
            }
        }
        if (editor instanceof IAutoAjustColumnEditor) {
            if (action.getId().equals(ID_AJUSTE_COLUMN)) {
                action.setChecked(((IAutoAjustColumnEditor) editor).getAutoResizeContext().isAutoResize());
            }
        }
    }

    private void updateHideOption(final IAction action) {
        if (editor instanceof ResultatEtapeEditor) {
            final ResultatEtapeEditor resultatEtapeEditor = (ResultatEtapeEditor) editor;
            if (action.getId().equals(ID_HIDE_DECLASSE)) {
                resultatEtapeEditor.getModel().setHideDeclasse(action.isChecked());
            }
            if (action.getId().equals(ID_HIDE_ABANDON)) {
                resultatEtapeEditor.getModel().setHideAbandon(action.isChecked());
            }
            if (action.getId().equals(ID_SIGNAL_ERROR)) {
                resultatEtapeEditor.getModel().setSignalError(action.isChecked());
            }
        }
        if (editor instanceof IAutoAjustColumnEditor) {
            if (action.getId().equals(ID_AJUSTE_COLUMN)) {
                ((IAutoAjustColumnEditor) editor).getAutoResizeContext().setAutoResize(action.isChecked());
            }
        }
    }

    public void run(final IAction action) {
        updateHideOption(action);
    }

    public void selectionChanged(final IAction action, final ISelection selection) {
    }
}
