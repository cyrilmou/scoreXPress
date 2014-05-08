package fr.cm.scorexpress.core.model;

import java.beans.PropertyChangeListener;

public interface ElementModelPropertyChange extends PropertyChangeListener {

    void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener);

    void addPropertyChangeListener(final PropertyChangeListener listener);

    void removePropertyChangeListener(final PropertyChangeListener listener);
}
