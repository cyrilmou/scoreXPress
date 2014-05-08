package fr.cm.common.widget.combobox;

import fr.cm.common.widget.StateListener;

public interface ComboStateListener extends StateListener {

    void textChange();

    void dataChange();

    void onSelection();
}
