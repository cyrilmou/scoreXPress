package fr.cm.scorexpress.core.model;

import fr.cm.scorexpress.core.model.impl.Date2;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public final class ObjUserChronos extends IData implements IChronos, Comparable<ObjUserChronos> {
    private static final long serialVersionUID = -3434519398304277739L;

    /**
     * Numéro de dossard
     */
    private String dossard = null;

    /**
     * Numéro de doigt sportIdent
     */
    private String puce = null;

    private String ordre = "";

    /**
     * Relevé des temps du dossard
     */
    private final LinkedList<ObjChrono> chronos = new LinkedList<ObjChrono>();

    static final        String VAR_DOSSARD            = "DOSSARD";
    static final        String VAR_DOSSARD_SPORTIDENT = "N° dép.";
    static final        String VAR_PUCE               = "PUCE";
    static final        String VAR_ORDRE              = "ORDRE";
    public static final String VAR_PREFIX_BALISE      = "BALISE";
    public static final String PREFIX_                = "USERCHRONOS_";

    public ObjUserChronos() {
    }

    public ObjUserChronos(final String puce) {
        setPuce(puce);
    }

    public ObjUserChronos(final String puce, final String dossard) {
        this(puce);
        this.dossard = dossard;
    }

    @Override
    public boolean addChrono(final ObjChrono chrono) {
        // Efface le chrono précedent afin de permettre de la remplacer
        chrono.setParent(this);
        return chronos.add(chrono);
    }

    @Override
    public boolean removeChrono(final ObjChrono chrono) {
        return chronos.remove(chrono);
    }

    @Override
    public Collection<ObjChrono> getChronos() {
        return chronos;
    }

    public ObjChrono getChrono(final ObjChrono c) {
        for (final ObjChrono chrono : chronos) {
            if (c.equals(chrono)) {
                return chrono;
            }
        }
        return null;
    }

    @Override
    public String getPrefix() {
        return ObjUserChronos.PREFIX_;
    }

    @Override
    public Object getInfoLocal(final String attribut) {
        if (VAR_DOSSARD.equalsIgnoreCase(attribut) || VAR_DOSSARD_SPORTIDENT.equalsIgnoreCase(attribut)) {
            return getDossard();
        }
        if (VAR_PUCE.equalsIgnoreCase(attribut)) {
            return getPuce();
        }
        final int index = attribut.indexOf(VAR_PREFIX_BALISE);
        if (index != -1) {
            final String nrBalise = attribut.substring(index + VAR_PREFIX_BALISE.length());
            try {
                final ObjChrono chrono = getChronoEnableHasPossible(nrBalise);
                if (chrono != null) {
                    return chrono.getTemps().toString();
                }
            } catch (Exception ex) {
                return null;
            }
        }
        if (VAR_ORDRE.equalsIgnoreCase(attribut)) {
            return ordre;
        }
        return null;
    }

    @Override
    public boolean setInfoLocal(final String attribut, final Object val) {
        if (VAR_DOSSARD.equalsIgnoreCase(attribut) || VAR_DOSSARD_SPORTIDENT.equalsIgnoreCase(attribut)) {
            dossard = "" + val;
            return true;
        } else if (VAR_PUCE.equalsIgnoreCase(attribut)) {
            puce = "" + val;
            return true;
        } else if (VAR_ORDRE.equalsIgnoreCase(attribut)) {
            setOrdre("" + val);
        }
        return false;
    }

    public void setOrdre(final String ordreStr) {
        ordre = ordreStr;
    }

    public String getOrdre() {
        return ordre;
    }

    public String getDossard() {
        return dossard;
    }

    public String getPuce() {
        return puce;
    }

    public void setDossard(final String dossard) {
        this.dossard = dossard;
    }

    public void setPuce(final String puce) {
        this.puce = puce;
    }

    public ObjChrono getChrono(final String numBalise) {
        for (final ObjChrono chrono : chronos) {
            try {
                if (numBalise.equalsIgnoreCase(chrono.getNumBalise())) {
                    return chrono;
                }
            } catch (Exception ex) {
            }
        }
        return null;
    }

    public ObjChrono getChronoEnableHasPossible(final String numBalise) {
        ObjChrono fund = null;
        for (final ObjChrono chrono : chronos) {
            try {
                if (numBalise.equalsIgnoreCase(chrono.getNumBalise())) {
                    if (chrono.isCancel()) {
                        fund = chrono;
                    } else {
                        return chrono;
                    }
                }
            } catch (Exception ex) {
            }
        }
        return fund;
    }

    public ObjChrono getChronoLast(final String numBalise) {
        final Iterator<ObjChrono> iter = chronos.descendingIterator();
        while (iter.hasNext()) {
            final ObjChrono c = iter.next();
            try {
                if (numBalise.equalsIgnoreCase(c.getNumBalise())) {
                    return c;
                }
            } catch (Exception ex) {
            }
        }
        return null;
    }

    public ObjChrono getChronoDepart() {
        for (final ObjChrono chrono : chronos) {
            if (chrono instanceof ObjChronoDepart) {
                return chrono;
            }
        }
        return null;
    }

    public ObjChrono getChronoArrivee() {
        final Iterator<ObjChrono> iter = chronos.iterator();
        while (iter.hasNext()) {
            final ObjChrono c = iter.next();
            if (c instanceof ObjChronoArrivee) {
                return c;
            }
        }
        return null;
    }

    public boolean equals(final Object object) {
        try {
            final ObjUserChronos info = (ObjUserChronos) object;
            return getPuce() == null && info.getPuce() == null || getPuce() != null && getPuce().equals(info.getPuce());

        } catch (Exception ex) {
        }
        return false;
    }

    @Override
    public int compareTo(final ObjUserChronos object) {
        try {
            return object.getPuce().compareTo(getPuce());
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visitUserChronos(this, data);
    }

    public void setDepart(final Date2 startTime) {
        boolean fund = false;
        for (final ObjChrono chrono : chronos) {
            if (chrono instanceof ObjChronoDepart) {
                chrono.setTemps(startTime);
                fund = true;
            }
        }
        if (!fund) {
            addChrono(new ObjChronoDepart(startTime));
        }
    }

    public void setArrivee(final Date2 endTime) {
        boolean fund = false;
        for (final ObjChrono chrono : chronos) {
            if (chrono instanceof ObjChronoArrivee) {
                chrono.setTemps(endTime);
                fund = true;
            }
        }
        if (!fund) {
            addChrono(new ObjChronoArrivee(endTime));
        }
    }
}
