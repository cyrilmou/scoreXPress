package fr.cm.common.widget.table;

public interface TableColumnModifier<T> {

    String getProperty();

    void modify(T element, String property, Object value);

    Object getValue(T element, String property);

    boolean canModify(T element, String property);
}
