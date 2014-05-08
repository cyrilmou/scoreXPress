package fr.cm.scorexpress.core.model;

import fr.cm.scorexpress.core.model.impl.Date2;
import static fr.cm.scorexpress.core.model.impl.DateFactory.createDate;

public class ObjChrono extends IData {
    private static final long serialVersionUID = 3671369196661391371L;
    public static final String VAR_CHRONO_DOSSARD = "DOSSARD";
    public static final String VAR_CHRONO_DOSSARD_SPORTIDENT = "N° dép.";
    public static final String VAR_CHRONO_NUMBALISE = "NUMBALISE";
    public static final String VAR_CHRONO_HEURE = "HEURE";

    private String numBalise;
    private Date2 temps;
    private boolean cancel = false;

    public ObjChrono(final String numBalise) {
        this.numBalise = numBalise;
    }

    public ObjChrono(final IData infoDossard, final String numBalise) {
        this.numBalise = numBalise;
        parent = infoDossard;
    }

    public ObjChrono(final String numBalise, final String date) {
        this(numBalise);
        temps = createDate(date);
    }

    public void setTemps(final Date2 temps) {
        this.temps = temps;
    }

    public Date2 getTemps() {
        return temps;
    }

    public String getNumBalise() {
        try {
            return new Integer(numBalise) + "";
        } catch (Exception ex) {
            return numBalise;
        }
    }

    public void setNumBalise(final String numBalise) {
        this.numBalise = numBalise;
    }

    public Object getInfoLocal(final String attribut) {
        if (ObjUserChronos.VAR_PUCE.equalsIgnoreCase(attribut)) {
            if (parent != null && parent instanceof ObjUserChronos) {
                return ((ObjUserChronos) parent).getPuce();
            }
        }
        if (VAR_CHRONO_NUMBALISE.equalsIgnoreCase(attribut)) {
            return getNumBalise();
        }
        if (VAR_CHRONO_HEURE.equalsIgnoreCase(attribut)) {
            return getTemps();
        }
        if (VAR_CHRONO_DOSSARD.equalsIgnoreCase(attribut)
                || VAR_CHRONO_DOSSARD_SPORTIDENT.equalsIgnoreCase(attribut)) {
            return getDossard();
        }
        return null;
    }

    private String getDossard() {
        if (parent != null && parent instanceof ObjUserChronos) {
            return ((ObjUserChronos) parent).getDossard();
        }
        return null;
    }

    public String getPrefix() {
        return "CHRONO_";
    }

    public boolean setInfoLocal(final String attribut, final Object val) {
        if (VAR_CHRONO_NUMBALISE.equalsIgnoreCase(attribut)) {
            numBalise = "" + val;
            return true;
        }
        if (VAR_CHRONO_HEURE.equalsIgnoreCase(attribut)) {
            setTemps(createDate("" + val));
            return true;
        }
        return false;
    }

    public boolean isNull() {
        return getTemps() == null || getTemps().isNull();
    }

    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visite(this, data);
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(final boolean cancel) {
        this.cancel = cancel;
    }

    public String toString() {
        return '<' + numBalise + ",tps=" + temps + ',' + cancel + '>';
    }
}
