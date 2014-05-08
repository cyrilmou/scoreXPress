package fr.cm.scorexpress.ihm.action;

import static fr.cm.common.workbench.SelectionUtils.firstSelection;
import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.getScoreXPressPlugin;
import static fr.cm.scorexpress.ihm.editor.ChronosEditor.CHRONOS_EDITOR_ID;
import fr.cm.scorexpress.ihm.editor.input.EtapeEditorInput;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.*;

public class ChronosAction implements IObjectActionDelegate {
    public static final String CHRONO_ACTION_ID = "fr.cm.chronos.actions.ChronosAction";

    private IStructuredSelection selection = null;
    private final AutoResizeColumn autoResizeContext;

    public ChronosAction() {
        autoResizeContext = new AutoResizeColumn();
    }

    public void run(final IAction action) {
        final IWorkbenchPage page = getScoreXPressPlugin().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        final IEditorInput input = createInput(firstSelection(selection, ObjStep.class), autoResizeContext);
        try {
            page.openEditor(input, CHRONOS_EDITOR_ID);
        } catch (PartInitException e) {
            throw new RuntimeException("Editor not loaded : " + CHRONOS_EDITOR_ID);
        }
    }

    public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
    }

    public void selectionChanged(final IAction action, final ISelection incoming) {
        if (incoming instanceof IStructuredSelection) {
            selection = (IStructuredSelection) incoming;
        }
    }

    private static IEditorInput createInput(final ObjStep step, final AutoResizeColumn autoResizeContext) {
        return new EtapeEditorInput(step, CHRONOS_EDITOR_ID, autoResizeContext);
    }
}
