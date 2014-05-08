package fr.cm.scorexpress.ihm.view;

import fr.cm.scorexpress.applicative.ProjectManager;
import fr.cm.scorexpress.core.model.AbstractSteps;
import fr.cm.scorexpress.core.model.IData;
import fr.cm.scorexpress.core.model.IUsers;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.ArrayList;
import java.util.Collection;

public class NavigatorContentProvider implements ITreeContentProvider {
    public void dispose() {
    }

    public Object[] getChildren(final Object element) {
        final Collection<Object> children = new ArrayList<Object>();
        if (element instanceof ProjectManager) {
            children.addAll(((ProjectManager) element).getProjects());
        }
        if (element instanceof AbstractSteps) {
            children.addAll(((AbstractSteps) element).getSteps());
        }
        return children.toArray();
    }

    public Object[] getElements(final Object element) {
        return getChildren(element);
    }

    public Object getParent(final Object element) {
        return ((IData) element).getParent();
    }

    public boolean hasChildren(final Object element) {
        boolean hasChildren = false;
        if (element instanceof IUsers) {
            hasChildren = true;
        }
        if (element instanceof AbstractSteps) {
            if (!((AbstractSteps) element).getSteps().isEmpty())
                hasChildren = true;
        }
        return hasChildren;
    }

    public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {
    }
}
