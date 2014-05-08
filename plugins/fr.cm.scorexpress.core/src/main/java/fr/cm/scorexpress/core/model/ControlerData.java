package fr.cm.scorexpress.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public abstract class ControlerData implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Collection<IControlerListener> controlers;

    public ControlerData() {
        controlers = new ArrayList<IControlerListener>();
    }

    private void fireChanged(final IData obj, final String type, final String property) {
        for (final IControlerListener controler : controlers) {
            controler.dataChanged(obj, type, property);
        }
    }

    public void hasChanged(final IData obj, final String property) {
        fireChanged(obj, IControlerListener.CHANGED, property);
    }

    public void hasAdd(final IData obj) {
        fireChanged(obj, IControlerListener.ADDED, "");
    }

    public void hasRemove(final IData obj) {
        fireChanged(obj, IControlerListener.REMOVED, "");
    }

    public void addView(final IControlerListener listener) {
        controlers.add(listener);
    }

    public void removeView(final IControlerListener listener) {
        controlers.remove(listener);
    }
}
