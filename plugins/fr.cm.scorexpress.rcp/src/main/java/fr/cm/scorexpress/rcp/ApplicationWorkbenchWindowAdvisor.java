package fr.cm.scorexpress.rcp;

import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IPageService;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.part.EditorInputTransfer;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }

    public void preWindowOpen() {
        super.preWindowOpen();
        final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(700, 550));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowProgressIndicator(true);
        configurer.setShowFastViewBars(true);
        configurer.setShowMenuBar(true);
        configurer.setTitle("ScoreXPress");
        configurer.addEditorAreaTransfer(EditorInputTransfer.getInstance());
        configurer.configureEditorAreaDropListener(new EditorAreaDropAdapter(configurer.getWindow()));
    }

    static class EditorAreaDropAdapter extends DropTargetAdapter {
        private final IPageService window;

        EditorAreaDropAdapter(final IPageService window) {
            this.window = window;
        }

        public void drop(final DropTargetEvent event) {
            super.drop(event);
            if (EditorInputTransfer.getInstance().isSupportedType(event.currentDataType)) {
                final EditorInputTransfer.EditorInputData[] editorInputs =
                        (EditorInputTransfer.EditorInputData[]) event.data;
                for (final EditorInputTransfer.EditorInputData inputData : editorInputs) {
                    try {
                        window.getActivePage().openEditor(inputData.input, inputData.editorId);
                    } catch (PartInitException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
