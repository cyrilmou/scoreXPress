package fr.cm.common.widget.combobox;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings({"NoopMethodInAbstractClass"})
public abstract class ComboLabelProvider<T> implements ILabelProvider, ComboComparator<T> {
    public Image getImage(final Object element) {
        return getImgCombo((T) element);
    }

    public abstract Image getImgCombo(final T element);

    public String getText(final Object element) {
        return getTextCombo((T) element);
    }

    public abstract String getTextCombo(T element);

    public void addListener(final ILabelProviderListener listener) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty(final Object element, final String property) {
        return true;
    }

    public void removeListener(final ILabelProviderListener listener) {
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    public int compare(final ComboLabelProvider<T> renderer, final T elem1, final T elem2) {
        final String var2 = renderer.getText(elem1);
        final String var1 = renderer.getText(elem2);
        try {
            final Comparable<Integer> value1 = new Integer(var1);
            final Integer value2 = new Integer(var2);
            return value1.compareTo(value2);
        } catch (Exception ignored) {
        }
        return var1.compareTo(var2);
    }
}
