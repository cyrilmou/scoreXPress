package fr.cm.common.widget.table;

import fr.cm.common.widget.table.TableStateListener.Direction;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.common.widget.table.TableStateListener.Direction.ASCENDING;
import static fr.cm.common.widget.table.TableStateListener.Direction.DESCENDING;

public class TableColumnModels<T> {
    private final List<TableColumnModel<T>> columns        = newArrayList();
    private final List<TableColumnModel<T>> lastColumnSort = newArrayList();
    private final Collection<TableStateListener<T>> stateListeners;

    public TableColumnModels(final Collection<TableStateListener<T>> stateListeners) {
        this.stateListeners = stateListeners;
    }

    public void addColumn(final TableColumnModel<T> column) {
        columns.add(column);
    }

    public int compare(final T elem1, final T elem2) {
        for (final TableColumnModel<T> column : lastColumnSort) {
            final int result = column.compare(elem1, elem2);
            if (result == 0) {
                continue;
            }
            if (column.getDirection().equals(DESCENDING)) {
                return result * -1;
            } else {
                return result;
            }
        }
        return 0;
    }

    public void sort(final TableColumnModel<T> column) {
        if (lastColumnSort.isEmpty() || !(lastColumnSort.indexOf(column) == 0)) {
            column.setDirection(ASCENDING);
            lastColumnSort.add(0, column);
            for (final TableStateListener<T> stateListener : stateListeners) {
                stateListener.onColumnSort(ASCENDING, column);
            }
        } else {
            final Direction newDirection = inverse(column.getDirection());
            column.setDirection(newDirection);
            lastColumnSort.remove(column);
            lastColumnSort.add(0, column);
            for (final TableStateListener<T> stateListener : stateListeners) {
                stateListener.onColumnSort(newDirection, column);
            }
        }
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    private static Direction inverse(final Direction d) {
        if (d.equals(ASCENDING)) {
            return DESCENDING;
        } else {
            return ASCENDING;
        }
    }

    public void autoResizeAll() {
        for (final TableStateListener<T> stateListener : stateListeners) {
            stateListener.onAutoResizeAllColumn();
        }
    }

    public void autoRemoveEmptyColumn() {
        for (final TableStateListener<T> stateListener : stateListeners) {
            stateListener.onAutoRemoveEmptyColumn();
        }
    }

    Object getModifiedValue(final T element, final String property) {
        return getColumn(property).getModifiedValue(element, property);
    }

    void modify(final T element, final String property, final Object value) {
        getColumn(property).modify(element, property, value);
        for (final TableStateListener<T> stateListener : stateListeners) {
            stateListener.onRowModify(element);
        }
    }

    boolean canModify(final T element, final String property) {
        final TableColumnModel<T> column = getColumn(property);
        if (column == null) {
            return false;
        }
        return column.canModify(element, property);
    }

    public TableColumnModel<T> getColumn(final String property) {
        for (final TableColumnModel<T> column : columns) {
            if (StringUtils.equals(property, column.getProperty())) {
                return column;
            }
        }
        return null;
    }

    public int indexOf(final TableColumnModel<T> columnModel) {
        return columns.indexOf(columnModel);
    }

    public String[] getProperties() {
        final Collection<String> properties = newArrayList();
        for (final TableColumnModel<T> column : columns) {
            properties.add(column.getProperty());
        }
        return properties.toArray(new String[properties.size()]);
    }

    public CellEditor[] getCellEditors() {
        final Collection<CellEditor> cellEditors = newArrayList();

        int i = 0;
        while (i < columns.size()) {
            cellEditors.add(new TextCellEditor() {
                @Override
                protected Object doGetValue() {
                    return super.doGetValue();
                }
            });
            i++;
        }
        return cellEditors.toArray(new CellEditor[cellEditors.size()]);
    }
}
