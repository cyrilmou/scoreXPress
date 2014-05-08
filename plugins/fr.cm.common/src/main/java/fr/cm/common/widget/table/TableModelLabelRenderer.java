package fr.cm.common.widget.table;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public abstract class TableModelLabelRenderer<T> implements ITableLabelProvider, ColumnsComparator<T> {
    private final TableModel<T> tableModel;

    public TableModelLabelRenderer(final TableModel<T> tableModel) {
        this.tableModel = tableModel;
    }

    final public Image getColumnImage(final Object element, final int columnIndex) {
        return getImage((T) element, columnIndex);
    }

    abstract public Image getImage(T element, int columnIndex);

    final public String getColumnText(final Object element, final int columnIndex) {
        return getText((T) element, columnIndex);
    }

    abstract public String getText(T element, int columnIndex);

    public void addListener(final ILabelProviderListener listener) {
    }

    public void dispose() {
    }

    final public boolean isLabelProperty(final Object element, final String property) {
        return isLabel((T) element, property);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public boolean isLabel(final T element, final String property) {
        return false;
    }

    public void removeListener(final ILabelProviderListener listener) {
    }

    public int compare(
            final ITableLabelProvider renderer, final T elem1, final T elem2, final TableColumnModel<T> columnModel) {
        final int columnIndex = tableModel.getColumnIndex(columnModel);
        return compare(renderer, elem1, elem2, columnIndex);
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    public int compare(final ITableLabelProvider renderer, final T elem1, final T elem2, final int columnIndex) {
        final String var2 = renderer.getColumnText(elem1, columnIndex);
        final String var1 = renderer.getColumnText(elem2, columnIndex);
        try {
            final Comparable<Integer> value1 = new Integer(var1);
            final Integer value2 = new Integer(var2);
            return value1.compareTo(value2);
        } catch (Exception ignored) {
        }
        return var1.compareTo(var2);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public boolean canModify(final Object element, final String property) {
        return false;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public Object getValue(final Object element, final String property) {
        return null;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void modify(final Object element, final String property, final Object value) {

    }
}
