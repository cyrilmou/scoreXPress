package fr.cm.common.widget.table;

import org.eclipse.jface.viewers.ITableLabelProvider;

public interface ColumnsComparator<T> extends ITableLabelProvider {

    public int compare(ITableLabelProvider renderer, T elem1, T elem2, TableColumnModel<T> columnIndex);
}
