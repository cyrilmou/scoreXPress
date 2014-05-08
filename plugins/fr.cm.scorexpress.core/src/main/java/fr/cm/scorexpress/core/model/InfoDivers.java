package fr.cm.scorexpress.core.model;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.stripToEmpty;

import java.io.Serializable;

public class InfoDivers implements Comparable<InfoDivers>, Serializable {

    private static final long serialVersionUID = -2980324073552249235L;

    protected final String attribut;
    protected final Object info;
    protected final boolean temp;

    public InfoDivers(final String attribut, final Object info) {
        this(attribut, info, false);
    }

    public InfoDivers(final String attribut, final Object info, final boolean tmp) {
        this.attribut = attribut;
        this.info = info;
        temp = tmp;
    }

    public String getAttribut() {
        return attribut;
    }

    public Object getInfo() {
        return info;
    }

    public String getInfoStr() {
        if (info != null) {
            return info + EMPTY;
        } else {
            return null;
        }
    }

    public boolean isTemp() {
        return temp;
    }

    public Object getCode() {
        return attribut.toUpperCase();
    }

    public String toString() {
        return EMPTY + info;
    }

    public int compareTo(final InfoDivers object) {
        return attribut.compareToIgnoreCase(object.attribut);
    }

    public boolean equals(final Object object) {
        if (info == null) {
            return false;
        }
        final InfoDivers inf = (InfoDivers) object;
        if (inf.info == null) {
            return false;
        }
        return stripToEmpty(info + EMPTY).equalsIgnoreCase(stripToEmpty(inf.info + EMPTY));
    }

}
