package fr.cm.scorexpress.ihm.action;

import fr.cm.scorexpress.ihm.view.NavigateurView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class ViewActionAjouterManif implements IViewActionDelegate {

    public static final String ID_AJOUTER = "fr.cm.chronos.actions.ViewActionAjouterManif";
    public static final String ID_SUPPRIMER = "fr.cm.chronos.actions.ViewActionSupprimerManif";
    public static final String ID_NEW = "fr.cm.chronos.actions.ViewActionNewManif";
    private NavigateurView view = null;

    public void init(final IViewPart view) {
        if (view instanceof NavigateurView) {
            this.view = (NavigateurView) view;
        }
    }

    public void run(final IAction action) {
        if (view != null) {
            if (action.getId().equals(ID_AJOUTER)) {
                view.addManifestation();
            }
            if (action.getId().equals(ID_SUPPRIMER)) {
                view.removeManifestation();
            }
            if (action.getId().equals(ID_NEW)) {
                view.newManifestation();
            }
        }
    }

    public void selectionChanged(final IAction action, final ISelection selection) {
    }
}
