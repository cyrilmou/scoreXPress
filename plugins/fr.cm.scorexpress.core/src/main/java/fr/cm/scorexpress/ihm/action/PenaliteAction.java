package fr.cm.scorexpress.ihm.action;

import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.getScoreXPressPlugin;
import static fr.cm.scorexpress.ihm.editor.PenaliteEditor.PENALITY_EDITOR_ID;
import fr.cm.scorexpress.ihm.editor.input.EtapeEditorInput;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.*;

import java.util.Iterator;

public class PenaliteAction implements IObjectActionDelegate {
    private final AutoResizeColumn autoResizeContext;

    public PenaliteAction() {
        autoResizeContext = new AutoResizeColumn();
    }

    public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
    }

    public void run(final IAction action) {
        final IPageService window = getScoreXPressPlugin().getWorkbench().getActiveWorkbenchWindow();
        final IStructuredSelection incoming = (IStructuredSelection) window.getActivePage().getSelection();
        for (Iterator<?> iter = incoming.iterator(); iter.hasNext();) {
            try {
                final ObjStep etape = (ObjStep) iter.next();
                final IEditorInput input = new EtapeEditorInput(etape, PENALITY_EDITOR_ID, autoResizeContext);
                final IWorkbenchPage page = window.getActivePage();
                page.openEditor(input, PENALITY_EDITOR_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void selectionChanged(final IAction action, final ISelection selection) {
    }
}
