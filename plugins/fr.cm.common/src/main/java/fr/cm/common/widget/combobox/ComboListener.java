package fr.cm.common.widget.combobox;

import fr.cm.common.widget.WidgetListener;

public interface ComboListener<T> extends WidgetListener {

    void onChange();

    void onSelectionChange(T item);
}
