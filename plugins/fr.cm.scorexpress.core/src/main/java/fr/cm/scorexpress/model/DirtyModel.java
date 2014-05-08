package fr.cm.scorexpress.model;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.EventListener;

public class DirtyModel implements EventListener {
    private boolean dirty;
    private final Collection<DirtyListener> listeners = newArrayList();

    public DirtyModel(final boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
        for (final DirtyListener listener : listeners) {
            listener.onDirty();
        }
    }

    public void addDirtyListener(final DirtyListener dirtyListener) {
        listeners.add(dirtyListener);
    }

    public void removeDirtyListener(final DirtyListener dirtyListener) {
        listeners.remove(dirtyListener);
    }

}
