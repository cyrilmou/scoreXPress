package fr.cm.common.widget.table;

import org.eclipse.jface.viewers.CellEditor;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;

public abstract class DefaultTableColumnModifier<T> implements TableColumnModifier<T> {
    private final String property;
    private final Collection<TableColumnModierListener<T>> listeners = newArrayList();

    public DefaultTableColumnModifier(final String property) {
        this.property = property;
    }

    @Override
    public String getProperty() {
        return property;
    }

    @Override
    public void modifyProperty(final T element, final String property, final Object value){
        modify(element, property, value);

        for (final TableColumnModierListener listener : listeners) {
            listener.onModified(element);
        }
    }


    public abstract void modify(final T element, final String property, final Object value);

    @Override
    public CellEditor getCellEditor() {
        return null;
    }

    public void addModifyListener(final TableColumnModierListener<T> listener) {
        listeners.add(listener);
    }
}
