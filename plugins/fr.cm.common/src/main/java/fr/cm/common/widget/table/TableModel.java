package fr.cm.common.widget.table;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import fr.cm.common.widget.CommonModel;
import java.util.Collection;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;

public class TableModel<T> extends CommonModel<TableStateListener<T>, TableListener<T>> {
    private final Collection<T> rows = newArrayList();
    private final TableColumnModels<T> columnModels;
    private final Predicate<T>         filter;
    private final Collection<T> rowSelected = newArrayList();

    public TableModel(final Predicate<T> filter) {
        this.filter = filter;
        columnModels = new TableColumnModels<T>(getStateListeners());
    }

    public TableModel() {
        this(Predicates.<T>alwaysTrue());
    }

    public TableColumnModels<T> getColumnModel() {
        return columnModels;
    }

    public void addRows(final Collection<T> rows) {
        this.rows.addAll(rows);
        for (final TableStateListener<T> listener : getStateListeners()) {
            listener.onRowsAdded(rows);
        }
        autoResizeColumns();
    }

    public void setRows(final Collection<T> rows) {
        this.rows.clear();
        this.rows.addAll(rows);
        for (final TableStateListener<T> listener : getStateListeners()) {
            listener.onRowsAdded(rows);
        }
        autoResizeColumns();
    }

    public void addRow(final T row) {
        rows.add(row);
        for (final TableStateListener<T> listener : getStateListeners()) {
            listener.onRowsAdded(asList(row));
        }
    }

    public void removeRows(final Collection<T> rows) {
        this.rows.removeAll(rows);
        for (final TableStateListener<T> listener : getStateListeners()) {
            listener.onRowsDeleted(rows);
        }
    }

    public void removeRow(final T row) {
        rows.remove(row);
        for (final TableStateListener<T> listener : getStateListeners()) {
            listener.onRowsDeleted(asList(row));
        }
    }

    public void clear() {
        rows.clear();
        for (final TableStateListener<T> listener : getStateListeners()) {
            listener.onTableDeleted();
        }
    }

    public void refilter() {
        for (final TableStateListener<T> listener : getStateListeners()) {
            listener.onFilterChange();
        }
    }

    public Collection<T> getRows() {
        return copyOf(rows);
    }

    public boolean shouldShow(final T row) {
        return filter.apply(row);
    }

    public void autoResizeColumns() {
        getColumnModel().autoResizeAll();
    }

    public void removeEmptyColumn() {
        getColumnModel().autoRemoveEmptyColumn();
    }

    public Collection<T> getSelection() {
        return unmodifiableCollection(rowSelected);
    }

    void selectionChanged(final Collection<T> selection) {
        rowSelected.removeAll(rowSelected);
        rowSelected.addAll(selection);
        for (final TableListener<T> listener : getWidgetListeners()) {
            listener.selectionChange();
        }
    }

    Object getValueToModify(final T element, final String property) {
        return columnModels.getModifiedValue(element, property);
    }

    void modify(final T element, final String property, final Object value) {
        columnModels.modify(element, property, value);
        for (final TableStateListener<T> listener : getStateListeners()) {
            listener.onRowModify(element);
        }
    }

    boolean canModify(final T element, final String property) {
        return columnModels.canModify(element, property);
    }

    public TableColumnModelBuilder<T> addColumn(final String property) {
        final TableColumnModelBuilder<T> builder = new TableColumnModelBuilder<T>(property, getStateListeners());
        columnModels.addColumn(builder.getModel());
        return builder;
    }

    public int getColumnIndex(final TableColumnModel<T> columnModel) {
        return columnModels.indexOf(columnModel);
    }
}
