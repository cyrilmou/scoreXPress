package fr.cm.scorexpress.core.model;

import fr.cm.scorexpress.core.model.impl.Date2;
import java.util.Comparator;
import java.util.Date;

import static fr.cm.scorexpress.core.model.impl.DateFactory.createDate;
import static java.lang.Boolean.valueOf;

public class ObjDossard extends IData<IData> implements Comparable<ObjDossard> {

    private static final long serialVersionUID = -5974609776040980149L;

    protected boolean team = false;
    protected String idUser;
    protected String num;
    protected String puce;
    private final Date2   penality     = createDate(0);
    private final Date2   temps        = createDate(true);
    private final Date2   bonification = createDate(0);
    private       boolean disqualifie  = false;
    private       boolean abandon      = false;
    private       String  category     = null;

    public static final String VAR_DOSSARD_PREFIX       = "DOSSARD_";
    public static final String VAR_DOSSARD_ABANDON      = "ABANDON";
    public static final String VAR_DOSSARD_DISQUALIFIER = "HC";
    public static final String VAR_DOSSARD_NUM          = "NUM";
    public static final String VAR_LIB                  = "LIB";
    public static final String VAR_DOSSARD_CATEGORIE    = "CATEGORIE";
    public static final String VAR_PUCE                 = "DOIGTSPORTIDENT";
    public static final String VAR_EPREUVE              = "EPREUVE";
    public static final String VAR_DOSSARD_BONIFICATION = "BONIFICATION";
    public static final String VAR_DOSSARD_PENALITY     = "PENALITE";
    public static final String VAR_TEMPS_IMPOSE         = "TEMPS";

    public ObjDossard(final String num) {
        this.num = num;
        penality.setAffichage(true, false, true);
        temps.setAffichage(false, true, true);
    }

    @Override
    public int compareTo(final ObjDossard object) {
        return new Integer(num).compareTo(new Integer(object.num));
    }

    public boolean equals(final Object object) {
        if (ObjDossard.class.isInstance(object)) {
            final ObjDossard d = (ObjDossard) object;
            return new Integer(num).equals(new Integer(d.num));
        }
        return false;
    }

    public static Comparator<ObjDossard> getComparator() {
        return new Comparator<ObjDossard>() {
            @Override
            public int compare(final ObjDossard object, final ObjDossard object1) {
                return object.compareTo(object1);
            }
        };
    }

    public String toString() {
        return "Dossard=" + num + ",abandon=" + abandon + ",disqu.=" + disqualifie + ",penality=" + penality;
    }

    public String getNum() {
        return num;
    }

    public boolean isAbandon() {
        return abandon;
    }

    @Override
    public Object getInfoLocal(final String attribut) {
        if (VAR_DOSSARD_ABANDON.equalsIgnoreCase(attribut)) {
            return isAbandon();
        }
        if (attribut.equalsIgnoreCase(VAR_DOSSARD_NUM)) {
            return num;
        }
        if (attribut.equalsIgnoreCase(VAR_PUCE)) {
            return puce;
        }
        if (attribut.equalsIgnoreCase(VAR_DOSSARD_CATEGORIE)) {
            return category;
        }
        if (attribut.equalsIgnoreCase(VAR_DOSSARD_PENALITY)) {
            return penality;
        }
        if (attribut.equalsIgnoreCase(VAR_TEMPS_IMPOSE)) {
            temps.setShowNull(false);
            return temps;
        }
        if (attribut.equalsIgnoreCase(VAR_DOSSARD_BONIFICATION)) {
            return bonification;
        }
        if (attribut.equalsIgnoreCase(VAR_DOSSARD_ABANDON)) {
            return abandon;
        }
        if (attribut.equalsIgnoreCase(VAR_DOSSARD_DISQUALIFIER)) {
            return disqualifie;
        }
        return null;
    }

    @Override
    public boolean setInfoLocal(final String attribut, final Object val) {
        if (attribut.equalsIgnoreCase(VAR_DOSSARD_NUM)) {
            final String newNum;
            try {
                newNum = Integer.parseInt("" + val) + "";
            } catch (Exception ex) {
                return true;
            }
            if (parent != null && parent instanceof IDossards) {
                if (!((IDossards) parent).getDossards().contains(new ObjDossard(newNum))) {
                    setIdUser(newNum);
                    return true;
                }
            }
            return true;
        }
        if (attribut.equalsIgnoreCase(VAR_PUCE)) {
            setPuce("" + val);
            return true;
        }
        if (attribut.equalsIgnoreCase(VAR_DOSSARD_ABANDON)) {
            setAbandon(valueOf("" + val));
            return true;
        }
        if (attribut.equalsIgnoreCase(VAR_DOSSARD_DISQUALIFIER)) {
            setDisqualifie(Boolean.valueOf("" + val));
            return true;
        }
        if (attribut.equalsIgnoreCase(VAR_DOSSARD_CATEGORIE)) {
            setCategory("" + val);
            return true;
        }
        if (attribut.equalsIgnoreCase(VAR_DOSSARD_BONIFICATION)) {
            setBonification(createDate("" + val));
            return true;
        }
        if (attribut.equalsIgnoreCase(VAR_DOSSARD_PENALITY)) {
            setPenality(createDate("" + val));
            return true;
        }
        if (attribut.equalsIgnoreCase(VAR_TEMPS_IMPOSE)) {
            setTemps(createDate("" + val));
            return true;
        }
        return false;
    }

    @Override
    public String getPrefix() {
        return VAR_DOSSARD_PREFIX;
    }

    public String getPuce() {
        return puce;
    }

    public void setPuce(final String doigtSportIdent) {
        if (puce == null || !puce.equals(doigtSportIdent)) {
            puce = doigtSportIdent;
            modifyCalculData();
            hasChanged(getControler(), this, VAR_PUCE);
        }
    }

    @Override
    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visite(this, data);
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(final String idUser) {
        if (idUser == null || !idUser.equals(idUser)) {
            firePropertyChange(VAR_DOSSARD_NUM, this.idUser, this.idUser = idUser);
        }
    }

    public Date2 getPenality() {
        return penality;
    }

    public void setPenality(final Date penality) {
        if (penality != null) {
            this.penality.setTime(penality.getTime());
            firePropertyChange(VAR_DOSSARD_PENALITY, null, this.penality);
        }
    }

    public void setTemps(final Date2 temps) {
        if (temps != null && !temps.equals(this.temps)) {
            this.temps.setTime(temps.getTime());
            temps.setShowNull(false);
            firePropertyChange(VAR_TEMPS_IMPOSE, null, temps);
        } else {
            modifyCalculData();
        }
    }

    public Date2 getTemps() {
        return temps;
    }

    public Date2 getBonification() {
        return bonification;
    }

    public void setBonification(final Date bonification) {
        if (bonification != null && !bonification.equals(this.bonification)) {
            this.bonification.setTime(bonification.getTime());
            firePropertyChange(VAR_DOSSARD_BONIFICATION, null, this.bonification);
        }
    }

    public boolean isDisqualifie() {
        return disqualifie;
    }

    public void setDisqualifie(final boolean disqualifie) {
        firePropertyChange(VAR_DOSSARD_DISQUALIFIER, this.disqualifie, this.disqualifie = disqualifie);
    }

    public void setAbandon(final boolean abandon) {
        firePropertyChange(VAR_DOSSARD_DISQUALIFIER, this.abandon, this.abandon = abandon);
    }

    public boolean isTeam() {
        return team;
    }

    public void setTeam(final boolean team) {
        firePropertyChange("Team", this.team, this.team = team);
    }

    public void setCategory(final String category) {
        firePropertyChange(VAR_DOSSARD_CATEGORIE, this.category, this.category = category);
    }

    public String getCategory() {
        return category;
    }
}
