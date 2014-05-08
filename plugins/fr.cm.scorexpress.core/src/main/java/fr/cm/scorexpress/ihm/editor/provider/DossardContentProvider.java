package fr.cm.scorexpress.ihm.editor.provider;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.Collection;

public class DossardContentProvider implements IStructuredContentProvider {
    public void dispose() {
    }

    public Object[] getElements(final Object inputElement) {
        return ((Collection<?>) inputElement).toArray();
    }

    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    }
}
