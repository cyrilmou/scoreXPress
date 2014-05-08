package fr.cm.common.widget.table;

import fr.cm.common.widget.StateListener;
import java.util.Collection;

public interface TableStateListener<T> extends StateListener {
    public static enum Direction {
        ASCENDING, DESCENDING
    }

    void onAutoResizeAllColumn();

    void onAutoRemoveEmptyColumn();

    void onColumnSort(final Direction newDirection, final TableColumnModel<T> columnIndex);

    void onFilterChange();

    void onRowsAdded(final Collection<T> rows);

    void onRowsDeleted(final Collection<T> rows);

    void onRowModify(T row);

    void onTableDeleted();
}
