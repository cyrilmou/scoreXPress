package fr.cm.common.widget.table;

import fr.cm.common.widget.WidgetListener;

public interface TableListener<T> extends WidgetListener {

    void selectionChange();

    void modify(T element, int column);
}
