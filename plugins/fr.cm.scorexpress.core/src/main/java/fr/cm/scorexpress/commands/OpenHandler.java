package fr.cm.scorexpress.commands;

import fr.cm.scorexpress.applicative.ProjectManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import static org.eclipse.ui.handlers.HandlerUtil.getActiveWorkbenchWindow;

public class OpenHandler extends AbstractHandler {

    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final IWorkbenchWindow window = getActiveWorkbenchWindow(event);

        final FileDialog fileDialog = new FileDialog(window.getShell(),
                SWT.OPEN);
        fileDialog.setFilterExtensions(new String[]{"*.XML;*.xml;*.raid", "*.raid"});
        fileDialog.setFilterNames(new String[]{"Fichiers chronosRAID (*.xml,*.raid)", "Projet ChronosRAID (*.raid)"});
        final String fileName = fileDialog.open();
        if (fileName != null) {
            ProjectManager.openProject(fileName);
        }
        return null;
    }

}
