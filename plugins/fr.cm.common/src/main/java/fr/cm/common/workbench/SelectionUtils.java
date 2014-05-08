package fr.cm.common.workbench;

import org.eclipse.jface.viewers.IStructuredSelection;

public class SelectionUtils {
    public static <T> T firstSelection(final IStructuredSelection selection, final Class<T> klass) {
        return (T) selection.getFirstElement();
    }
}
