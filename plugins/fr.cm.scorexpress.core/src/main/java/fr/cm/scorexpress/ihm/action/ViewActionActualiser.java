package fr.cm.scorexpress.ihm.action;

import fr.cm.scorexpress.ihm.view.NavigateurView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class ViewActionActualiser implements IViewActionDelegate {
    private NavigateurView view = null;

    public void init(final IViewPart view) {
        if (view instanceof NavigateurView) {
            this.view = (NavigateurView) view;
        }
    }

    public void run(final IAction action) {
        if (view != null) {
            view.refresh();
        }
    }

    public void selectionChanged(final IAction action, final ISelection selection) {
    }

}
