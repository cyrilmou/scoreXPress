package fr.cm.common.widget.composite;

import fr.cm.common.widget.CommonModel;

public class FormModel extends CommonModel<FormStateListener, FormListener> {
    private String text;

    public FormModel(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
        for (final FormStateListener listener : getStateListeners()) {
            listener.textChange();
        }
    }
}
