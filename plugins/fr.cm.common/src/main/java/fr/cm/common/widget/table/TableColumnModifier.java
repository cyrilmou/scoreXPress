package fr.cm.common.widget.table;

import org.eclipse.jface.viewers.CellEditor;

public interface TableColumnModifier<T> {

    String getProperty();

    void modifyProperty(T element, String property, Object value);

    Object getValue(T element, String property);

    boolean canModify(T element, String property);

    CellEditor getCellEditor();
}
