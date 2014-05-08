package fr.cm.common.widget.text;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.StringUtils.EMPTY;
import java.util.Collection;

public class TextModel<T> {
    private final Collection<TextStateListener<T>>  stateListeners  = newArrayList();
    private final Collection<TextModifyListener<T>> modifyListeners = newArrayList();
    private       String                            value           = EMPTY;
    private       boolean                           block           = false;

    public TextModel() {
    }

    public TextModel(final String value) {
        this.value = value;
    }

    public void setText(final String text) {
        block = true;
        for (final TextStateListener<T> listener : stateListeners) {
            listener.onTextChange(text);
        }
        value = text;
        block = false;
    }

    public String getText() {
        return value;
    }

    public void valueChanged(final String text) {
        for (final TextModifyListener<T> modifyListener : modifyListeners) {
            modifyListener.onModify(text);
        }
        value = text;
    }

    void addStateListener(final TextStateListener<T> listener) {
        stateListeners.add(listener);
    }

    void removeStateListener(final TextStateListener<T> listener) {
        stateListeners.remove(listener);
    }

    public void addModifyListener(final TextModifyListener<T> listener) {
        modifyListeners.add(listener);
    }

    public void removeModifyListener(final TextModifyListener<T> listener) {
        modifyListeners.remove(listener);
    }

    public void textChanged(final String text) {
        if (!block) {
            value = text;
            for (final TextModifyListener<T> listener : modifyListeners) {
                listener.onModify(text);
            }
        }
    }

    public void focusLost() {
        for (final TextModifyListener<T> listener : modifyListeners) {
            listener.onExit();
        }
    }

    public void focusGained() {
        for (final TextModifyListener<T> listener : modifyListeners) {
            listener.onEntry();
        }
    }
}
