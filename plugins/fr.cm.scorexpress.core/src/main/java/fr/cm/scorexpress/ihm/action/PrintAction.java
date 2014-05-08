package fr.cm.scorexpress.ihm.action;

import fr.cm.scorexpress.applicative.ProjectManager;
import fr.cm.scorexpress.ihm.print.IPrintable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public class PrintAction implements IEditorActionDelegate {
    private IPrintable printableEditor = null;

    public void setActiveEditor(final IAction action, final IEditorPart targetEditor) {
        if (targetEditor instanceof IPrintable) {
            action.setEnabled(true);
            printableEditor = (IPrintable) targetEditor;
        } else {
            action.setEnabled(false);
        }
    }

    public void run(final IAction action) {
        final boolean allowPrint = ProjectManager.verifyLicence(ProjectManager.ALLOW_PRINT);
        if (allowPrint) {
            printableEditor.print();
        }
    }

    public void selectionChanged(final IAction action, final ISelection selection) {
    }

}
