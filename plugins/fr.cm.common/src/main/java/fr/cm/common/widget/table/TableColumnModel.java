package fr.cm.common.widget.table;

import fr.cm.common.widget.table.TableStateListener.Direction;
import org.eclipse.jface.viewers.CellEditor;

import static fr.cm.common.widget.table.TableStateListener.Direction.ASCENDING;
import java.util.Collection;

public class TableColumnModel<T> {
    private Direction direction = ASCENDING;
    private boolean autoResize = false;
    private TableColumnModifier<T> modifier = null;
    private int width = 0;
    private final Collection<TableStateListener<T>> stateListeners;
    private String property;
    private ColumnComparator<T> comparator;

    public TableColumnModel(final String property, final Collection<TableStateListener<T>> stateListeners) {
        this.stateListeners = stateListeners;
        this.property = property;
    }

    Direction getDirection() {
        return direction;
    }

    void setDirection(final Direction direction) {
        this.direction = direction;
    }

    boolean isAutoResize() {
        return autoResize;
    }

    void setAutoResize(final boolean autoResize) {
        this.autoResize = autoResize;
    }

    void setModifier(final TableColumnModifier<T> modifier) {
        this.modifier = modifier;
    }

    int getWidth() {
        return width;
    }

    void setWidth(final int width) {
        this.width = width;
    }

    public String getProperty() {
        return property;
    }

    public Object getModifiedValue(final T element, final String property) {
        if (modifier != null) {
            return modifier.getValue(element, property);
        }
        return null;
    }

    public void modify(final T element, final String property, final Object value) {
        if (modifier != null) {
            modifier.modifyProperty(element, property, value);
        }
    }

    public boolean canModify(final T element, final String property) {
        if (modifier != null) {
            return modifier.canModify(element, property);
        }
        return false;
    }

    public int compare(final T elem1, final T elem2) {
        return comparator.compare(elem1, elem2);
    }

    public void setComparator(final ColumnComparator<T> comparator) {
        this.comparator = comparator;
    }

    public CellEditor getCellEditor() {
        if( modifier !=null)
            return modifier.getCellEditor();
        return null;
    }
}
