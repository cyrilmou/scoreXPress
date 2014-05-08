package fr.cm.common.widget;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import static java.util.Collections.unmodifiableCollection;

public abstract class CommonModel<T extends StateListener, E extends WidgetListener> {
    private boolean enable = true;
    private final Collection<T> stateListeners = newArrayList();
    private final Collection<E> widgetListeners = newArrayList();

    protected CommonModel() {
    }

    public void setEnable(final boolean enable) {
        if (this.enable != enable) {
            this.enable = enable;
            fireOnEnable(enable);
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public void addStateListener(final T listener) {
        stateListeners.add(listener);
    }

    public void removeStateListener(final T listener) {
        stateListeners.remove(listener);
    }

    public void addWidgetListener(final E listener) {
        widgetListeners.add(listener);
    }

    public void removeWidgetListener(final E listener) {
        widgetListeners.remove(listener);
    }

    public Collection<T> getStateListeners() {
        return unmodifiableCollection(stateListeners);
    }

    public Collection<E> getWidgetListeners() {
        return unmodifiableCollection(widgetListeners);
    }

    private void fireOnEnable(final boolean enable) {
        for (final T stateListener : stateListeners) {
            stateListener.onEnable(enable);
        }
    }
}
