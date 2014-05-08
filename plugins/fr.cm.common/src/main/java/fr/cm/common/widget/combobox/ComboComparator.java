package fr.cm.common.widget.combobox;

public interface ComboComparator<T> {

    int compare(final ComboComparator<T> renderer, final T elem1, final T elem2);
}
