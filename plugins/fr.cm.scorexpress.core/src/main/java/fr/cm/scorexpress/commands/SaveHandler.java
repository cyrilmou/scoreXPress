package fr.cm.scorexpress.commands;

import fr.cm.scorexpress.applicative.ProjectManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class SaveHandler extends AbstractHandler {

    public Object execute(final ExecutionEvent event) throws ExecutionException {
        ProjectManager.saveAllProjects();
        return null;
    }
}
