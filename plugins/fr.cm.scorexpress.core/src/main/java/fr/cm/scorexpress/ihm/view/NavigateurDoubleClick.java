package fr.cm.scorexpress.ihm.view;

import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.model.ObjManifestation;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.editor.GeneralEditorModel;
import fr.cm.scorexpress.ihm.editor.ResultatEtapeModel;
import fr.cm.scorexpress.ihm.editor.StepEditor;
import fr.cm.scorexpress.ihm.editor.StepEditorModel;
import fr.cm.scorexpress.ihm.editor.input.GeneraleEditorInput;
import fr.cm.scorexpress.ihm.editor.input.ResultatEditorInput;
import fr.cm.scorexpress.ihm.editor.input.StepEditorInput;
import fr.cm.scorexpress.model.ManifModel;
import fr.cm.scorexpress.model.StepModel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPageService;
import org.eclipse.ui.IWorkbenchWindow;

import static fr.cm.scorexpress.ihm.editor.GeneralEditor.GENERAL_EDITOR_ID;
import static fr.cm.scorexpress.ihm.editor.ResultatEtapeEditor.RESULTAT_ETAPE_EDITOR_ID;

public class NavigateurDoubleClick extends MouseAdapter {

    private final Tree         tree;
    private final IPageService window;

    public static void applyNavigatorDoubleClick(final Tree tree, final IWorkbenchWindow window) {
        tree.addMouseListener(new NavigateurDoubleClick(tree, window));
    }

    NavigateurDoubleClick(final Tree tree, final IPageService window) {
        this.window = window;
        this.tree = tree;
    }

    public void mouseDoubleClick(final MouseEvent e) {
        final Object selection = (tree.getSelection())[0].getData();
        if (selection instanceof ObjStep) {
            final ObjStep step = (ObjStep) selection;
            if (step.isClassementInter() || step.isEpreuve()) {
                try {
                    final IEditorInput editorInput =
                            new ResultatEditorInput(new ResultatEtapeModel(new AutoResizeColumn(),
                                                                           new StepModel(step,
                                                                                         true,
                                                                                         new AutoResizeColumn()),
                                                                           0,
                                                                           new ManifModel(step.getManif())));
                    window.getActivePage().openEditor(editorInput, RESULTAT_ETAPE_EDITOR_ID);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                try {
                    final IEditorInput editorInput =
                            new StepEditorInput(new StepEditorModel(new StepModel(step, true, new AutoResizeColumn())));
                    window.getActivePage().openEditor(editorInput, StepEditor.STEP_EDITOR_ID);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (selection instanceof ObjManifestation) {
            final ObjManifestation manif = (ObjManifestation) selection;
            try {
                window.getActivePage()
                      .openEditor(new GeneraleEditorInput(new GeneralEditorModel(new ManifModel(manif))),
                                  GENERAL_EDITOR_ID);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
