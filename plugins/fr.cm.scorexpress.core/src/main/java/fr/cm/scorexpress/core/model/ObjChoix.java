package fr.cm.scorexpress.core.model;

import java.io.Serializable;

public class ObjChoix implements Serializable {
    private static final long serialVersionUID = 5955662747349880039L;

    public int valeur = 0;

    public String lib = null;

    public ObjChoix(final int valeur, final String lib) {
        this.valeur = valeur;
        this.lib = lib;
    }

    public String getLib() {
        return lib;
    }

    public int getValeur() {
        return valeur;
    }

    @Override
    public String toString() {
        return lib;
    }

    public Integer getValeurInteger() {
        return valeur;
    }

    public boolean equals(final Object obj) {
        if (obj != null && obj instanceof ObjChoix) {
            return ((ObjChoix) obj).getValeur() == valeur;
        } else {
            return obj != null && obj instanceof Integer && obj.equals(getValeurInteger());
        }
    }
}
