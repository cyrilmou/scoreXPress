package fr.cm.scorexpress.core;

import com.google.common.collect.Lists;
import java.util.Collection;

public class AutoResizeColumn {
    private boolean autoResize;
    private final Collection<AutoResizeListener> listeners = Lists.newArrayList();

    public AutoResizeColumn(final boolean autoResize) {
        this.autoResize = autoResize;
    }

    public AutoResizeColumn() {
        this(true);
    }

    public void addAutoResizeListener(final AutoResizeListener listener) {
        listeners.add(listener);
    }

    public void removeAutoResizeListener(final AutoResizeListener listener) {
        listeners.remove(listener);
    }

    public boolean isAutoResize() {
        return autoResize;
    }

    public void setAutoResize(final boolean autoResize) {
        this.autoResize = autoResize;
        stateChanged();
    }

    public static interface AutoResizeListener {
        public void autoResizeChanged(boolean autoResize);
    }

    private void stateChanged() {
        for (final AutoResizeListener listener : listeners) {
            listener.autoResizeChanged(autoResize);
        }
    }
}
