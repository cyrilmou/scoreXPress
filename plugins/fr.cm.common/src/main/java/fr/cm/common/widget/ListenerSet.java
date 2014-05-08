package fr.cm.common.widget;

import static com.google.common.collect.ImmutableList.copyOf;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;

public final class ListenerSet<T extends EventListener> implements Iterable<T> {
    private final Collection<T> listeners = new HashSet();

    public static <T extends EventListener> ListenerSet<T> newListenerSet() {
        return new ListenerSet<T>();
    }

    private ListenerSet() {
    }

    public Iterator<T> iterator() {
        return copyOf(listeners).iterator();
    }

    public void add(final T listener) {
        listeners.add(listener);
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    public void remove(final T listener) {
        listeners.remove(listener);
    }
}
