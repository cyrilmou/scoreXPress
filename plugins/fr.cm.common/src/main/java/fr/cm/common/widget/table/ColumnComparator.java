package fr.cm.common.widget.table;

public interface ColumnComparator<T> {

    public int compare(T elem1, T elem2);
}