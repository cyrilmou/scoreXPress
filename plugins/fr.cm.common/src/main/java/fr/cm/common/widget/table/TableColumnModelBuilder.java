package fr.cm.common.widget.table;

import java.util.Collection;

public class TableColumnModelBuilder<T> {
    private final TableColumnModel<T> model;

    TableColumnModelBuilder(final String property, final Collection<TableStateListener<T>> stateListeners) {
        model = new TableColumnModel<T>(property, stateListeners);
    }

    public TableColumnModel<T> getModel() {
        return model;
    }

    public TableColumnModelBuilder<T> withAutoResize(final boolean autoResize) {
        model.setAutoResize(autoResize);
        return this;
    }

    public TableColumnModelBuilder<T> withModifier(final TableColumnModifier<T> modifier) {
        model.setModifier(modifier);
        return this;
    }
}
