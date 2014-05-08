package fr.cm.common.widget.button;

import fr.cm.common.widget.StateListener;

public interface ButtonStateListener extends StateListener {

    void onLabelChanged();

    void onSelect();
}
