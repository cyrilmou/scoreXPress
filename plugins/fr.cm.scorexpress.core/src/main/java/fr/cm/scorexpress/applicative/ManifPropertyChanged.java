package fr.cm.scorexpress.applicative;

import fr.cm.scorexpress.core.model.IData;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public abstract class ManifPropertyChanged implements PropertyChangeListener {

    public void propertyChange(final PropertyChangeEvent evt) {
        manifChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue(), (IData<IData>) evt.getSource());
    }

    public abstract void manifChange(final String propertyName, final Object oldValue, final Object newValue,
                                     final IData<IData> iDataIData);

}
