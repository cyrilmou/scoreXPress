package fr.cm.common.workbench;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import static org.eclipse.ui.IWorkbenchActionConstants.MB_ADDITIONS;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

public class WorkbenchUtils {
    private WorkbenchUtils() {
    }

    public static void defineCopyViewSiteAction(final Display display, final IActionBars actionBars) {
        final IAction action = new WorkbenchCopyAction(display);
        actionBars.setGlobalActionHandler(action.getId(), action);
    }

    public static IWorkbenchPage getWorkbenchPage(final IWorkbenchPart editor) {
        return editor.getSite().getWorkbenchWindow().getActivePage();
    }

    public static boolean openEditor(final IWorkbenchPart editor, final IEditorInput input, final String editorId) {
        try {
            getWorkbenchPage(editor).openEditor(input, editorId);
            return true;
        } catch (PartInitException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void defineWorkbenchPopupMenu(final IWorkbenchPartSite viewSite, final Viewer viewer) {
        final MenuManager menuManager = new MenuManager();
        menuManager.add(new GroupMarker(MB_ADDITIONS));
        final Menu menu = menuManager.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        viewSite.registerContextMenu(menuManager, viewer);
    }
}
