package fr.cm.common.widget.table;

public abstract class DefaultTableColumnModifier<T> implements TableColumnModifier<T> {
    private final String property;

    public DefaultTableColumnModifier(final String property) {

        this.property = property;
    }

    @Override
    public String getProperty() {
        return property;
    }

    @Override
    public abstract void modify(final T element, final String property, final Object value);
}
