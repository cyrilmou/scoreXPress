package fr.cm.common.widget.button;

import fr.cm.common.widget.CommonModel;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class ButtonModel extends CommonModel<ButtonStateListener, ButtonListener> {
    private String label = EMPTY;
    private boolean selected = false;

    public ButtonModel(final String label) {
        this.label = label;
    }

    public ButtonModel() {
    }

    public void click() {
        for (final ButtonListener listener : getWidgetListeners()) {
            listener.click();
        }
    }

    public String getLabel() {
        return label;
    }

    public void setSelection(final boolean selected) {
        this.selected = selected;
        for (final ButtonStateListener stateListener : getStateListeners()) {
            stateListener.onSelect();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setLabel(final String label) {
        this.label = label;
        for (final ButtonStateListener stateListener : getStateListeners()) {
            stateListener.onLabelChanged();
        }
    }
}
