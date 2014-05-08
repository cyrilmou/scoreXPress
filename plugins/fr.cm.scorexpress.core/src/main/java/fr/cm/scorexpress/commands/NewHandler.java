package fr.cm.scorexpress.commands;

import static fr.cm.scorexpress.applicative.ProjectManager.newProject;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import static org.eclipse.ui.handlers.HandlerUtil.getActiveWorkbenchWindow;

public class NewHandler extends AbstractHandler {

    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final IWorkbenchWindow window = getActiveWorkbenchWindow(event);

        final FileDialog fileDialog = new FileDialog(window.getShell(), SWT.MULTI);
        fileDialog.setFilterExtensions(new String[]{"*.XML;*.raid", "*.raid"});
        fileDialog.setFilterNames(new String[]{"Fichiers chronosRAID (*.xml,*.raid)", "Projet ChronosRAID (*.raid)"});
        final String fileName = fileDialog.open();
        if (fileName != null) {
            newProject(fileName);
        }
        return null;
    }
}
