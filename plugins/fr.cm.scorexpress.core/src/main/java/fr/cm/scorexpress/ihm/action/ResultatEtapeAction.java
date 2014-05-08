package fr.cm.scorexpress.ihm.action;

import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.editor.ResultatEtapeModel;
import fr.cm.scorexpress.ihm.editor.input.ResultatEditorInput;
import fr.cm.scorexpress.model.ManifModel;
import fr.cm.scorexpress.model.StepModel;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import java.util.Iterator;

import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.getScoreXPressPlugin;
import static fr.cm.scorexpress.ihm.editor.ResultatEtapeEditor.RESULTAT_ETAPE_EDITOR_ID;

public class ResultatEtapeAction implements IActionDelegate {
    public static final String ID                     = "fr.cm.chronos.actions.ResultatEtapeAction";
    public static final String ID_EXPERT              = "fr.cm.chronos.actions.ResultatEtapeActionExpert";
    public static final String WITH_INTER_RESULT_MODE = "fr.cm.chronos.actions.WithInterResult";

    @Override
    public void run(final IAction action) {
        final int mode;
        if (action.getId().equals(ID_EXPERT)) {
            mode = 1;
        } else if (action.getId().equals(WITH_INTER_RESULT_MODE)) {
            mode = 2;
        } else {
            mode = 0;
        }
        final IWorkbenchWindow window = getScoreXPressPlugin().getWorkbench().getActiveWorkbenchWindow();
        final IStructuredSelection incoming = (IStructuredSelection) window.getActivePage().getSelection();
        for (Iterator<? extends ObjStep> iter = incoming.iterator(); iter.hasNext(); ) {
            final ObjStep step = iter.next();
            final IEditorInput input = new ResultatEditorInput(
                    new ResultatEtapeModel(new AutoResizeColumn(), new StepModel(step, true, new AutoResizeColumn()),
                                           mode, new ManifModel(step.getManif())));
            try {
                window.getActivePage().openEditor(input, RESULTAT_ETAPE_EDITOR_ID);
            } catch (PartInitException e) {
                throw new RuntimeException("Error: open editor " + RESULTAT_ETAPE_EDITOR_ID);
            }
        }
    }

    @Override
    public void selectionChanged(final IAction action, final ISelection selection) {
    }
}
