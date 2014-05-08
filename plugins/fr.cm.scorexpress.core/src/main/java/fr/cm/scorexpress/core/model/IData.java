package fr.cm.scorexpress.core.model;

import org.apache.commons.lang.StringUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.EMPTY;

public abstract class IData<T extends IData>
        implements AbstractInfoDivers<T>, Serializable, ElementModel, ElementModelPropertyChange {

    private static final long serialVersionUID = 1L;

    protected transient ControlerData controler;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    protected T parent;

    public boolean calculDataModified = true;

    protected           HashMap<Object, InfoDivers> infoDiverses             = new HashMap<Object, InfoDivers>();
    public static final String                      VAR_CALCUL_DATA_MODIFIED = "calculDataModified";
    public static final String                      VAR_STATE_CHANGED        = "StateChanged";

    protected IData() {
        propertyChangeSupport.addPropertyChangeListener(this);
    }

    public abstract String getPrefix();

    public abstract Object getInfoLocal(String attribut);

    public abstract boolean setInfoLocal(String attribut, Object val);

    public static String getLocalAttribut(final String prefix, String attribut) {
        if (attribut.startsWith(prefix) || attribut.indexOf('_') == -1) {
            final int index = attribut.indexOf('_');
            if (index != -1) {
                attribut = attribut.substring(index + 1);
            }
            return attribut;
        }
        return null;
    }

    public boolean isLocalAttribut(final String attribut) {
        return getLocalAttribut(getPrefix(), attribut) != null;
    }

    @Override
    public Object getInfo(final String attribut) {
        if (attribut == null) {
            return null;
        }
        final String attrib = getLocalAttribut(getPrefix(), attribut);
        if (attrib == null) {
            if (parent != null) {
                return parent.getInfo(attribut);
            }
            return null;
        }
        final Object obj = getInfoLocal(attrib);
        if (obj != null) {
            return obj;
        }
        try {
            final InfoDivers infoDivers = infoDiverses.get(attrib.toUpperCase());
            if (infoDivers != null) {
                return infoDivers.getInfo();
            }
        } catch (Exception ex) {
        }
        return null;
    }

    public <T> T getInfo(final String attribut, final Class<T> type) {
        final Object obj = getInfo(attribut);
        if (obj == null) {
            return (T) null;
        } else {
            return (T) obj;
        }
    }

    @Override
    public String getInfoStr(final String attribut) {
        final Object info = getInfo(attribut);
        if (info == null) {
            return EMPTY;
        }
        return info.toString();
    }

    public boolean isInfoNull(final String attribut) {
        final Object info = getInfo(attribut);
        return info == null || info.equals("") || info.equals("null");
    }

    @Override
    public Object setInfoTmp(final String attribut, final Object val) {
        return setInfo(attribut, val, true);
    }

    @Override
    public Object setInfo(final String attribut, final Object val) {
        return setInfo(attribut, val, false);
    }

    private Object setInfo(final String attribut, final Object val, final boolean tmp) {
        if (attribut == null) {
            return null;
        }
        final String attrib = getLocalAttribut(getPrefix(), attribut);
        if (attrib != null) {
            if (setInfoLocal(attrib, val)) {
                // hasChanged(getControler(), this, attribut);
            } else {
                setInfoDiverse(attrib, val, tmp);
            }
        } else {
            if (parent != null) {
                if (!parent.setInfoLocal(attribut, val)) {
                    parent.setInfo(attribut, val);
                }
            }
        }
        return getInfo(attribut);
    }

    public ControlerData getControler() {
        if (controler == null) {
            if (parent != null) {
                return parent.getControler();
            } else {
                return null;
            }
        }
        return controler;
    }

    public void setControler(final ControlerData controler) {
        this.controler = controler;
    }

    public InfoDivers setInfoDiverse(final String attribut, final Object val, final boolean tmp) {
        final InfoDivers info = new InfoDivers(attribut, val, tmp);
        setInfoDiverse(info);
        return info;
    }

    public InfoDivers setInfoDiverse(final String attribut, final Object val) {
        final InfoDivers info = new InfoDivers(attribut, val);
        setInfoDiverse(info);
        return info;
    }

    public void setInfoDiverse(final InfoDivers info) {
        final InfoDivers oldInfo = infoDiverses.get(info.getCode());
        if (oldInfo != null && oldInfo.equals(info)) {
            return;
        }
        infoDiverses.put(info.getCode(), info);
        if (!info.isTemp()) {
            hasChanged(controler, this, "" + info.getCode());
        }
    }

    @Override
    public Collection<InfoDivers> getInfoDiverses() {
        return infoDiverses.values();
    }

    public void setParent(final T parent) {
        this.parent = parent;
    }

    @Override
    public T getParent() {
        return parent;
    }

    @Override
    public Object getInstance() {
        return this;
    }

    @Override
    public ObjManifestation getManif() {
        if (this instanceof ObjManifestation) {
            return (ObjManifestation) this;
        } else {
            if (getParent() != null) {
                return getParent().getManif();
            } else {
                return null;
            }
        }
    }

    protected void hasChanged(final ControlerData controler, final IData obj, final String property) {
        if (controler != null) {
            controler.hasChanged(obj, property);
        }
        if (parent != null) {
            parent.hasChanged(parent.controler, obj, property);
        }
    }

    protected void hasAdd(final ControlerData controler, final IData obj) {
        if (controler != null) {
            controler.hasAdd(obj);
        }
        if (parent != null) {
            parent.hasAdd(parent.controler, obj);
        }
    }

    protected void hasRemove(final ControlerData controler, final IData obj) {
        if (controler != null) {
            controler.hasRemove(obj);
        }
        if (parent != null) {
            parent.hasRemove(parent.controler, obj);
        }
    }

    public void modifyCalculData() {
        if (parent != null) {
            parent.modifyCalculData();
        }
        if (!calculDataModified) {
            calculDataModified = true;

        }
    }

    public void fireStateChanged() {
        if (asList(propertyChangeSupport.getPropertyChangeListeners()).contains(this)) {
            propertyChangeSupport.removePropertyChangeListener(this);
            propertyChangeSupport.firePropertyChange(createEvent(VAR_STATE_CHANGED, false, true, this));
            propertyChangeSupport.addPropertyChangeListener(this);
        } else {
            propertyChangeSupport.firePropertyChange(createEvent(VAR_STATE_CHANGED, false, true, this));
        }
    }

    protected void fireLocalEventChanged(final String property, final Object old, final Object newObj) {
        fireLocalEventChanged(createEvent(property, old, newObj, null));
    }

    void fireLocalEventChanged(final PropertyChangeEvent event) {
        if (asList(propertyChangeSupport.getPropertyChangeListeners()).contains(this)) {
            propertyChangeSupport.removePropertyChangeListener(this);
            propertyChangeSupport.firePropertyChange(event);
            propertyChangeSupport.addPropertyChangeListener(this);
        } else {
            propertyChangeSupport.firePropertyChange(event);
        }
    }

    public boolean isCalculDataModify() {
        return calculDataModified;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        //  fireStateChanged();
        fireLocalEventChanged(event);
        // System.out.println("event: " + event.getPropertyName() + " - " + event.getSource());
    }

    @Override
    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(final String property, final Object oldValue, final Object newValue) {
        propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
    }

    protected static boolean propertyMatches(final String[] properties, final PropertyChangeEvent event) {
        for (final String property : properties) {
            if (StringUtils.equals(property, event.getPropertyName())) {
                return true;
            }
        }
        return false;
    }

    public PropertyChangeEvent createEvent(final String property, final Object old, final Object newObj, final Object id) {
        final PropertyChangeEvent changeEvent = new PropertyChangeEvent(this, property, old, newObj);
        changeEvent.setPropagationId(id);
        return changeEvent;
    }
}
