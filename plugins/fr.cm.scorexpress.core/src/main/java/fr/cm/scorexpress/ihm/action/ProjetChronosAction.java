package fr.cm.scorexpress.ihm.action;

import fr.cm.scorexpress.applicative.ProjectManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import static org.eclipse.swt.SWT.MULTI;
import static org.eclipse.swt.SWT.OPEN;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class ProjetChronosAction extends Action implements IWorkbenchAction, IWorkbenchWindowActionDelegate {
    public static final String ID_OPEN = "fr.cm.scorexpress.actions.open";
    public static final String ID_SAV = "fr.cm.scorexpress.actions.sav";
    public static final String ID_NEW = "fr.cm.scorexpress.actions.new";

    public ProjetChronosAction() {
    }

    public ProjetChronosAction(final IWorkbenchWindow window, final String id) {
        setId(id);
        setActionDefinitionId(id);
        if (ID_OPEN.equals(id)) {
            setImageDescriptor(ActionFactory.NEW.create(window).getImageDescriptor());
            setText("Ouvrir un fichier");
        }
        if (ID_SAV.equals(id)) {
            setImageDescriptor(ActionFactory.SAVE_ALL.create(window).getImageDescriptor());
            setText("Enregister le Projet");
        }
        if (ID_NEW.equals(id)) {
            setImageDescriptor(ActionFactory.OPEN_NEW_WINDOW.create(window).getImageDescriptor());
            setText("Nouveau projet");
        }
    }

    public void runWithEvent(final Event event) {
        super.runWithEvent(event);
        if (getId().equals(ID_OPEN)) {
            final FileDialog fileDialog = new FileDialog(event.display.getActiveShell(), OPEN);
            fileDialog.setFilterExtensions(new String[]{"*.XML;*.raid", "*.raid"});
            fileDialog.setFilterNames(new String[]{
                    "Fichiers chronosRAID (*.xml,*.raid)",
                    "Projet ChronosRAID (*.raid)"});
            final String fileName = fileDialog.open();
            if (fileName != null) {
                ProjectManager.openProject(fileName);
            }
        } else if (getId().equals(ID_SAV)) {
            ProjectManager.saveAllProjects();
        } else if (getId().equals(ID_NEW)) {
            final FileDialog fileDialog = new FileDialog(event.display.getActiveShell(), MULTI);
            fileDialog.setFilterExtensions(new String[]{"*.XML;*.raid", "*.raid"});
            fileDialog.setFilterNames(new String[]{
                    "Fichiers chronosRAID (*.xml,*.raid)",
                    "Projet ChronosRAID (*.raid)"});
            final String fileName = fileDialog.open();
            if (fileName != null) {
                ProjectManager.newProject(fileName);
            }
        }
    }

    public void dispose() {
    }

    public void init(final IWorkbenchWindow window) {
    }

    public void run(final IAction action) {
        setId(action.getId());
        run();
    }

    public void selectionChanged(final IAction action, final ISelection selection) {
    }

}
