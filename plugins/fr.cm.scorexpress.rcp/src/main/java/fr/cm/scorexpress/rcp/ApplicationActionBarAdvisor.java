package fr.cm.scorexpress.rcp;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import static org.eclipse.ui.actions.ActionFactory.COPY;
import static org.eclipse.ui.actions.ActionFactory.PASTE;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    public ApplicationActionBarAdvisor(final IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(final IWorkbenchWindow window) {
        register(COPY.create(window));
        register(PASTE.create(window));
    }

    protected void fillMenuBar(final IMenuManager menuBar) {
    }

}
