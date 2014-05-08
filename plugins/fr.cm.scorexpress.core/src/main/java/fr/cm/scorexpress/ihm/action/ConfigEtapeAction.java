package fr.cm.scorexpress.ihm.action;

import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.editor.StepEditorModel;
import fr.cm.scorexpress.ihm.editor.input.StepEditorInput;
import fr.cm.scorexpress.model.StepModel;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.getScoreXPressPlugin;
import static fr.cm.scorexpress.ihm.editor.StepEditor.STEP_EDITOR_ID;

public class ConfigEtapeAction implements IActionDelegate {
    public static final String ID = "fr.cm.chronos.actions.ConfigEtapeAction";

    private IStructuredSelection selection = null;

    @Override
    public void run(final IAction action) {
        try {
            final IWorkbenchWindow window = getScoreXPressPlugin().getWorkbench().getActiveWorkbenchWindow();
            final ISelection incoming = window.getSelectionService().getSelection();
            if (incoming instanceof IStructuredSelection) {
                selection = (IStructuredSelection) incoming;
            }
            if (selection != null) {
                final Object item = selection.getFirstElement();
                final ObjStep step = (ObjStep) item;
                final IWorkbenchPage page = window.getActivePage();
                page.openEditor(new StepEditorInput(new StepEditorModel(new StepModel(step,
                                                                                      true,
                                                                                      new AutoResizeColumn()))),
                                STEP_EDITOR_ID);
            }
        } catch (PartInitException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectionChanged(final IAction action, final ISelection incoming) {
        if (incoming instanceof IStructuredSelection) {
            selection = (IStructuredSelection) incoming;
        }
    }
}
