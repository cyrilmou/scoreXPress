package fr.cm.common.widget.combobox;

import fr.cm.common.widget.CommonModel;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableCollection;

public class ComboModel<T> extends CommonModel<ComboStateListener, ComboListener<T>> {
    private final ArrayList<T> items          = newArrayList();
    private       int          indexSelection = -1;
    private       String       text           = "";

    public ComboModel() {
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
        for (final ComboStateListener comboListener : getStateListeners()) {
            comboListener.textChange();
        }
    }

    public void setItems(final Collection<T> items) {
        this.items.clear();
        this.items.addAll(items);
        if (indexSelection >= this.items.size()) {
            indexSelection = 0;
        }
        for (final ComboStateListener comboListener : getStateListeners()) {
            comboListener.dataChange();
            comboListener.textChange();
        }
    }

    public Collection<T> getItems() {
        return unmodifiableCollection(items);
    }

    public int getIndexSelection() {
        return indexSelection;
    }

    public T getSelection() {
        if (indexSelection != -1) {
            return items.get(indexSelection);
        }
        return null;
    }

    int compare(final ComboComparator<T> comparator, final T element1, final T element2) {
        return comparator.compare(comparator, element1, element2);
    }

    void setSelection(final T selection) {
        final int newSelection = items.indexOf(selection);
        if (newSelection != indexSelection) {
            indexSelection = newSelection;
            for (final ComboListener<T> comboListener : getWidgetListeners()) {
                comboListener.onSelectionChange(selection);
            }
        }
    }

    void modifyText(final String text) {
        this.text = text;
        for (final ComboListener<T> comboListener : getWidgetListeners()) {
            comboListener.onChange();
        }
    }

    void selectionChanged(final T item) {
        setSelection(item);
        for (final ComboListener<T> comboListener : getWidgetListeners()) {
            comboListener.onSelectionChange(item);
        }
    }
}
