package fr.cm.common.widget.label;

import fr.cm.common.widget.CommonModel;

public class LabelModel extends CommonModel<LabelStateListener, LabelListener> {
    private String label;
    private String tooltip;

    public LabelModel(final String label) {
        this.label = label;
        tooltip = "";
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
        for (final LabelStateListener listener : getStateListeners()) {
            listener.onChanged();
        }
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(final String tooltip) {
        this.tooltip = tooltip;
    }

    public void setVisible(boolean visible) {
        for (final LabelStateListener listener : getStateListeners()) {
            listener.setVisible(visible);
        }
    }
}
