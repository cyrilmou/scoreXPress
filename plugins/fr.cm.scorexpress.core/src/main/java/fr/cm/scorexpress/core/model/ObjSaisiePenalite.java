package fr.cm.scorexpress.core.model;

import fr.cm.scorexpress.core.model.impl.Date2;
import fr.cm.scorexpress.core.model.impl.DateFactory;

import static java.lang.Integer.parseInt;
import java.util.Comparator;

public class ObjSaisiePenalite extends IData {
    private static final long serialVersionUID = 8361928699496281422L;

    private String dossard;

    private int valeur = 0;

    public static final String VAR_DOSSARD = "DOSSARD";

    public static final String VAR_VALEUR = "VALEUR";

    public static final String VAR_DUREE = "DUREE";

    public ObjSaisiePenalite(final String dossard) {
        this.dossard = dossard;
    }

    public int compareTo(final Object object) {
        return dossard.compareTo(((ObjSaisiePenalite) object).dossard);
    }

    public int getValeur() {
        return valeur;
    }

    public String getDossard() {
        return dossard;
    }

    public void setDossard(final String dossard) {
        this.dossard = dossard;
    }

    public void setValeur(final int valeur) {
        this.valeur = valeur;
    }

    public void setValeurStr(final String valeur) {
        try {
            this.valeur = parseInt(valeur);
        } catch (Exception ex) {
        }
    }

    public static Comparator<ObjSaisiePenalite> getComparator() {
        return new Comparator<ObjSaisiePenalite>() {
            public int compare(final ObjSaisiePenalite object,
                               final ObjSaisiePenalite object1) {
                return object.toString().compareTo(object1.toString());
            }
        };
    }

    @Override
    public String getPrefix() {
        return "SAISIE";
    }

    @Override
    public Object getInfoLocal(final String attribut) {
        if (attribut.equalsIgnoreCase(VAR_DOSSARD)) {
            return dossard;
        }
        if (attribut.equalsIgnoreCase(VAR_VALEUR)) {
            return valeur;
        }
        if (attribut.equalsIgnoreCase(VAR_DUREE)) {
            if (getParent() instanceof ObjPenalite) {
                final ObjPenalite penalite = (ObjPenalite) getParent();
                final Date2 duree = DateFactory.createDate(penalite.getPenalite().getTime()
                        * valeur);
                duree.setAffichage(true, false, true);
                return duree;
            }
        }
        return null;
    }

    @Override
    public boolean setInfoLocal(final String attribut, final Object val) {
        if (attribut.equalsIgnoreCase(VAR_DOSSARD)) {
            setDossard(val + "");
            return true;
        }
        if (attribut.equalsIgnoreCase(VAR_VALEUR)) {
            setValeurStr(val + "");
            return true;
        }
        return false;
    }

    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visite(this, data);
    }
}
